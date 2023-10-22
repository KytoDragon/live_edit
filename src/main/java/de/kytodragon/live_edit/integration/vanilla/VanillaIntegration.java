package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.integration.Integration;
import de.kytodragon.live_edit.integration.PacketRegistry;
import de.kytodragon.live_edit.mixin_interfaces.BrewingRecipeRegistryInterface;
import de.kytodragon.live_edit.recipe.RecipeManager;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.*;

public class VanillaIntegration implements Integration {

    public net.minecraft.world.item.crafting.RecipeManager vanilla_recipe_manager;
    public ITagManager<Item> forge_tag_manager;

    private Registry<Item> vanilla_item_registry;
    private MinecraftServer server;

    private NewServerData server_data;

    private Map<Item, Integer> current_burn_times;

    @Override
    public void registerManipulators(RecipeManager manager) {
        // Deal with recipe types in the standard recipe manager that are not beeing handled by a manipulator.
        // This makes shure we do not delete recipes we do not know about.
        for (net.minecraft.world.item.crafting.RecipeType<?> recipeType : ForgeRegistries.RECIPE_TYPES.getValues()) {
            manager.addRecipeManipulator(this, new RecipeType("Dummy", recipeType), new DummyRecipeManipulator<>(recipeType));
        }

        manager.addRecipeManipulator(this, RecipeType.CRAFTING, new CraftingRecipeManipulator());
        manager.addRecipeManipulator(this, RecipeType.SMELTING, new CoockingRecipeManipulator<>(net.minecraft.world.item.crafting.RecipeType.SMELTING, SmeltingRecipe::new));
        manager.addRecipeManipulator(this, RecipeType.BLASTING, new CoockingRecipeManipulator<>(net.minecraft.world.item.crafting.RecipeType.BLASTING, BlastingRecipe::new));
        manager.addRecipeManipulator(this, RecipeType.SMOKING, new CoockingRecipeManipulator<>(net.minecraft.world.item.crafting.RecipeType.SMOKING, SmokingRecipe::new));
        manager.addRecipeManipulator(this, RecipeType.CAMPFIRE_COOKING, new CoockingRecipeManipulator<>(net.minecraft.world.item.crafting.RecipeType.CAMPFIRE_COOKING, CampfireCookingRecipe::new));
        manager.addRecipeManipulator(this, RecipeType.STONECUTTING, new StoneCuttingRecipeManipulator());
        manager.addRecipeManipulator(this, RecipeType.SMITHING, new SmithingRecipeManipulator());

        manager.addRecipeManipulator(this, RecipeType.TAGS, new TagManipulator());

        manager.addRecipeManipulator(this, RecipeType.BURN_TIME, new BurnTimeManipulator());
        manager.addRecipeManipulator(this, RecipeType.BREWING, new BrewingRecipeManipulator());
        manager.addRecipeManipulator(this, RecipeType.COMPOSTING, new CompostManipulator());

        MinecraftForge.EVENT_BUS.addListener(this::onFuelBurnTimeRequest);

        VanillaUpdatePacket.registerPacketHandler();
    }

    @Override
    @SuppressWarnings({"deprecation"})
    public void initServer(MinecraftServer server) {
        this.server = server;
        vanilla_recipe_manager = server.getRecipeManager();
        vanilla_item_registry = Registry.ITEM;

        forge_tag_manager = ForgeRegistries.ITEMS.tags();
        Objects.requireNonNull(forge_tag_manager);
    }

    @Override
    public void shutdownServer() {

        current_burn_times = null;
        server_data = null;

        forge_tag_manager = null;
        vanilla_item_registry = null;
        vanilla_recipe_manager = null;
        server = null;
    }

    public void onFuelBurnTimeRequest(FurnaceFuelBurnTimeEvent event) {
        if (current_burn_times != null) {
            Integer burnTime = current_burn_times.get(event.getItemStack().getItem());
            if (burnTime != null) {
                event.setBurnTime(burnTime.intValue());
            } else {
                event.setBurnTime(0);
            }
        }
    }

    @Override
    public void prepareReload() {
        server_data = new NewServerData();
        Ingredient.invalidateAll();
    }

    @Override
    public void reload() {
        Map<TagKey<Item>, List<Holder<Item>>> vanilla_map = new HashMap<>();
        server_data.new_tags.forEach(tag -> {
            List<Holder<Item>> list = tag.content.stream().map(ForgeRegistries.ITEMS::getHolder).map(Optional::orElseThrow).toList();
            vanilla_map.put(tag.key, list);
        });
        vanilla_item_registry.bindTags(vanilla_map);
        Blocks.rebuildCache();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.TagsUpdatedEvent(server.registryAccess(), false, false));

        if (FMLEnvironment.dist.isDedicatedServer()) {
            // In single-player this is already done via the ClientboundUpdateRecipesPacket
            vanilla_recipe_manager.replaceRecipes(server_data.new_recipes);
        }
        PlayerList player_list = server.getPlayerList();
        player_list.broadcastAll(new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(server.registryAccess())));
        player_list.broadcastAll(new ClientboundUpdateRecipesPacket(server_data.new_recipes));

        VanillaUpdatePacket client_packet = new VanillaUpdatePacket(server_data.new_burn_times, server_data.new_compostables, server_data.new_potions);
        if (FMLEnvironment.dist.isDedicatedServer()) {
            // Burn times, compostables and new potion recipes are updates the same between client and server,
            // so just accept the packet on the server side to update these settings.
            acceptClientPacket(client_packet);
        }
        PacketRegistry.INSTANCE.send(PacketDistributor.ALL.noArg(), client_packet);

        server_data = null;
    }

    public void acceptClientPacket(Object o) {
        if (o instanceof VanillaUpdatePacket packet) {

            Map<Item, Integer> burn_time = new HashMap<>();
            packet.new_burn_times().forEach(burn -> burn_time.put(burn.item(), burn.burn_time()));
            current_burn_times = burn_time;

            ComposterBlock.COMPOSTABLES.clear();
            packet.new_compostables().forEach(compost -> ComposterBlock.COMPOSTABLES.put(compost.item(), compost.compastChance()));

            ((BrewingRecipeRegistryInterface)new BrewingRecipeRegistry()).live_edit_mixin_setRecipes(packet.new_potions());
        }
    }

    public void addNewRecipes(Collection<? extends Recipe<?>> recipes) {
        server_data.new_recipes.addAll(recipes);
    }

    public void addNewTags(Collection<Tag<Item>> tags) {
        server_data.new_tags.addAll(tags);
    }

    public void addNewBurnTimes(Collection<BurnTime> burnTimes) {
        server_data.new_burn_times.addAll(burnTimes);
    }

    public void addNewCompostables(Collection<CompostChance> compostables) {
        server_data.new_compostables.addAll(compostables);
    }

    public void addNewPotions(Collection<IBrewingRecipe> potions) {
        server_data.new_potions.addAll(potions);
    }

    private static class NewServerData {

        private final List<Recipe<?>> new_recipes = new ArrayList<>();
        private final List<Tag<Item>> new_tags = new ArrayList<>();
        private final List<BurnTime> new_burn_times = new ArrayList<>();
        private final List<CompostChance> new_compostables = new ArrayList<>();
        private final List<IBrewingRecipe> new_potions = new ArrayList<>();
    }
}
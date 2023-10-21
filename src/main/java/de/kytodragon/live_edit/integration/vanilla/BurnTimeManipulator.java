package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.recipe.GeneralManipulationData;
import de.kytodragon.live_edit.recipe.IRecipeManipulator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class BurnTimeManipulator extends IRecipeManipulator<ResourceLocation, BurnTime, VanillaIntegration> {
    @Override
    public ResourceLocation getKey(BurnTime burnTime) {
        return integration.forge_item_registry.getKey(burnTime.item());
    }

    @Override
    public BurnTime manipulate(BurnTime burnTime, GeneralManipulationData data) {
        if (data.itemsToReplace.containsKey(burnTime.item())) {
            burnTime = new BurnTime(data.itemsToReplace.get(burnTime.item()), burnTime.burn_time());
        }
        return burnTime;
    }

    @Override
    public Collection<BurnTime> getCurrentRecipes() {
        List<BurnTime> result = new ArrayList<>();
        for (Item item : integration.forge_item_registry.getValues()) {

            int burnTime = ForgeHooks.getBurnTime(new ItemStack(item), null);
            if (burnTime > 0)
                result.add(new BurnTime(item, burnTime));
        }
        return result;
    }

    @Override
    public Optional<BurnTime> getRecipe(ResourceLocation key) {
        Item item = integration.forge_item_registry.getValue(key);
        if (item == null)
            return Optional.empty();
        return Optional.of(new BurnTime(item, ForgeHooks.getBurnTime(new ItemStack(item), null)));
    }

    @Override
    public void prepareReload(Collection<BurnTime> burnTimes) {
        integration.addNewBurnTimes(burnTimes);
    }
}

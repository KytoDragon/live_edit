package de.kytodragon.live_edit.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyLootTable;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.editing.gui.loot_tables.LootTableEditingMenu;
import de.kytodragon.live_edit.editing.gui.recipes.RecipeEditingMenu;
import de.kytodragon.live_edit.recipe.*;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.network.NetworkHooks;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.kytodragon.live_edit.LiveEditMod.LOGGER;

public class Command {

    private static final int COMMAND_PERMISSION = 0;

    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        ArgumentTypeInfos.registerByClass(RecipeArgument.class, SingletonArgumentInfo.contextFree(RecipeArgument::new));
        ArgumentTypeInfos.registerByClass(RecipeTypeArgument.class, SingletonArgumentInfo.contextFree(RecipeTypeArgument::new));
        ArgumentTypeInfos.registerByClass(RecipeJsonArgument.class, SingletonArgumentInfo.contextFree(RecipeJsonArgument::new));
        ArgumentTypeInfos.registerByClass(LootTableJsonArgument.class, SingletonArgumentInfo.contextFree(LootTableJsonArgument::new));

        event.getDispatcher().register(
            LiteralArgumentBuilder.<CommandSourceStack>literal("live-edit")
            .requires(cs -> cs.hasPermission(COMMAND_PERMISSION))
            .then(reloadCommand())
            .then(listRecipesCommand(event.getBuildContext()))
            .then(deleteRecipesCommand())
            .then(addRecipeCommand())
            .then(replaceCommands(event.getBuildContext()))
            .then(encodeRecipesCommand())
            .then(openGUICommand())
            .then(testRecipesCommand())
        );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reloadCommand() {
        return Commands.literal("reload")
            .executes(ctx -> {
                RecipeManager.instance.manipulateAllRecipesAndReload();
                return 1;
            });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> listRecipesCommand(CommandBuildContext buildContext) {
        return Commands.literal("list")
            // TODO split into result, ingredient and all, maybe using an enum
            .then(Commands.literal("result")
                .then(Commands.argument("item", new ItemArgument(buildContext))
                    .executes(ctx -> {
                        Item item = ItemArgument.getItem(ctx, "item").getItem();

                        Stream<IRecipeManipulator<ResourceLocation, ?, ?>> manipulators = RecipeManager.instance.manipulators.values().stream().filter(IRecipeManipulator::isRealImplementation);
                        Stream<Pair<RecipeType, ResourceLocation>> matching_recipes = manipulators.flatMap(manipulator -> findItem(manipulator, item));
                        String list = matching_recipes.map(pair -> "\n\u2022 " + pair.getLeft().name() + " " + pair.getRight().toString())
                                            .collect(Collectors.joining());
                        ctx.getSource().sendSuccess(Component.translatable("live_edit.commands.recipe.list", list), false);
                        return 0;
                    })
                )
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> deleteRecipesCommand() {
        return Commands.literal("delete")
            .then(Commands.argument("type", new RecipeTypeArgument(true))
                .then(Commands.argument("recipe", new RecipeArgument())
                    .executes(ctx -> {
                        RecipeType type = RecipeTypeArgument.getRecipeType(ctx, "type");
                        ResourceLocation recipe_key = null;
                        if (type == RecipeType.ALL) {
                            for (RecipeType actual_type : RecipeManager.instance.manipulators.keySet()) {
                                ResourceLocation actual_recipe_key = RecipeArgument.getRecipeOrNull(ctx, actual_type, "recipe");
                                if (actual_recipe_key != null) {
                                    RecipeManager.instance.markRecipeForDeletion(actual_type, actual_recipe_key);
                                    recipe_key = actual_recipe_key;
                                }
                            }
                            if (recipe_key == null) {
                                RecipeArgument.throwError(ctx, "recipe");
                                return 0;
                            }
                        } else {
                            recipe_key = RecipeArgument.getRecipe(ctx, type, "recipe");
                            RecipeManager.instance.markRecipeForDeletion(type, recipe_key);
                        }

                        ctx.getSource().sendSuccess(Component.translatable("live_edit.commands.recipe.marked_for_deletion", recipe_key.toString()), false);
                        return 1;
                    })
                )
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> encodeRecipesCommand() {
        return Commands.literal("encode")
            .then(Commands.argument("type", new RecipeTypeArgument())
                .then(Commands.argument("recipe", new RecipeArgument())
                    .executes(ctx -> {
                        RecipeType type = RecipeTypeArgument.getRecipeType(ctx, "type");
                        ResourceLocation recipe_key = RecipeArgument.getRecipe(ctx, type, "recipe");
                        if (type == RecipeType.LOOT_TABLE) {
                            MyLootTable table = getEncodedLootTable(RecipeManager.instance.manipulators.get(type), recipe_key);
                            if (table != null)
                                ctx.getSource().sendSuccess(Component.literal(table.toJsonString()), false);
                        } else {
                            MyRecipe recipe = getEncodedRecipe(RecipeManager.instance.manipulators.get(type), recipe_key);
                            if (recipe != null)
                                ctx.getSource().sendSuccess(Component.literal(recipe.toJsonString()), false);
                        }
                        return 1;
                    })
                )
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> replaceCommands(CommandBuildContext buildContext) {
        return Commands.literal("replace")
            .then(Commands.literal("item")
                .then(Commands.argument("item", new ItemArgument(buildContext))
                    .then(Commands.argument("replacement", new ItemArgument(buildContext))
                        .executes(ctx -> {
                            Item item = ItemArgument.getItem(ctx, "item").getItem();
                            Item replacement = ItemArgument.getItem(ctx, "replacement").getItem();
                            RecipeManager.instance.markItemForReplacement(item, replacement);

                            ctx.getSource().sendSuccess(Component.translatable("live_edit.commands.item.marked_for_replacement", item.toString(), replacement.toString()), false);
                            return 1;
                        })
                    )
                )
            )
            .then(Commands.literal("recipe")
                .then(Commands.literal(RecipeType.LOOT_TABLE.name())
                    .then(Commands.argument("loot_able", new RecipeArgument())
                        .then(Commands.argument("replacement", new LootTableJsonArgument())
                            .executes(ctx -> {
                                ResourceLocation loot_table_key = RecipeArgument.getRecipe(ctx, RecipeType.LOOT_TABLE, "loot_able");
                                MyLootTable loot_table = LootTableJsonArgument.getLootTable(ctx, "replacement");

                                RecipeManager.instance.markLootTableForReplacement(loot_table_key, loot_table);

                                ctx.getSource().sendSuccess(Component.translatable("live_edit.commands.loot_table.marked_for_replacement", loot_table_key.toString()), false);
                                return 1;
                            })
                        )
                    )
                )
                .then(Commands.argument("type", new RecipeTypeArgument())
                    .then(Commands.argument("recipe", new RecipeArgument())
                        .then(Commands.argument("replacement", new RecipeJsonArgument())
                            .executes(ctx -> {
                                RecipeType type = RecipeTypeArgument.getRecipeType(ctx, "type");
                                ResourceLocation recipe_key = RecipeArgument.getRecipe(ctx, type, "recipe");
                                MyRecipe recipe = RecipeJsonArgument.getRecipe(ctx, "replacement");

                                RecipeManager.instance.markRecipeForReplacement(type, recipe_key, recipe);

                                ctx.getSource().sendSuccess(Component.translatable("live_edit.commands.recipe.marked_for_replacement", recipe_key.toString()), false);
                                return 1;
                            })
                        )
                    )
                )
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> addRecipeCommand() {
        return Commands.literal("add")
            .then(Commands.literal("recipe")
                .then(Commands.argument("type", new RecipeTypeArgument())
                    .then(Commands.argument("recipe", new RecipeArgument())
                        .then(Commands.argument("replacement", new RecipeJsonArgument())
                            .executes(ctx -> {
                                RecipeType type = RecipeTypeArgument.getRecipeType(ctx, "type");
                                ResourceLocation recipe_key = RecipeArgument.getRecipe(ctx, type, "recipe");
                                MyRecipe recipe = RecipeJsonArgument.getRecipe(ctx, "replacement");

                                RecipeManager.instance.markRecipeForAddition(type, recipe_key, recipe);

                                ctx.getSource().sendSuccess(Component.translatable("live_edit.commands.recipe.marked_for_addition", recipe_key.toString()), false);
                                return 1;
                            })
                        )
                    )
                )
            );
    }

    private static <T> Stream<Pair<RecipeType, ResourceLocation>> findItem(IRecipeManipulator<ResourceLocation, T, ?> manipulator, Item item) {
        return manipulator.getCurrentRecipes().stream().map(manipulator::encodeRecipe).filter(recipe -> {
            if (recipe == null)
                return false;

            if (recipe.results != null) {
                for (MyResult result : recipe.results) {
                    if (result instanceof MyResult.ItemResult itemResult) {
                        if (itemResult.item.getItem() == item) {
                            return true;
                        }
                    }
                }
            }
            if (recipe.ingredients != null) {
                for (MyIngredient ingredient : recipe.ingredients) {
                    if (ingredient instanceof MyIngredient.ItemIngredient itemIngredient) {
                        if (itemIngredient.item.getItem() == item) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }).map(recipe -> Pair.of(recipe.type, recipe.id));
    }

    private static <T> MyRecipe getEncodedRecipe(IRecipeManipulator<ResourceLocation, T, ?> manipulator, ResourceLocation key) {
        Optional<T> recipe = manipulator.getRecipe(key);
        if (recipe.isEmpty())
            return null;

        return manipulator.encodeRecipe(recipe.get());
    }

    private static <T> MyLootTable getEncodedLootTable(IRecipeManipulator<ResourceLocation, T, ?> manipulator, ResourceLocation key) {
        Optional<T> table = manipulator.getRecipe(key);
        if (table.isEmpty())
            return null;

        try {
            return LootTableConverter.convertLootTable((LootTable) table.get());
        } catch (UnsupportedOperationException e) {
            LOGGER.error("Failed to convert loot table: ", e);
            return null;
        }
    }

    private static ArgumentBuilder<CommandSourceStack, ?> openGUICommand() {
        return Commands.literal("edit")
            .then(Commands.argument("type", new RecipeTypeArgument())
                .then(Commands.argument("recipe", new RecipeArgument())
                    .executes(ctx -> {
                        ServerPlayer serverPlayer = ctx.getSource().getPlayer();
                        if (serverPlayer != null) {
                            RecipeType type = RecipeTypeArgument.getRecipeType(ctx, "type");
                            ResourceLocation recipe_key = RecipeArgument.getRecipe(ctx, type, "recipe");

                            if (type == RecipeType.LOOT_TABLE) {
                                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
                                    (int containerId, Inventory inventory, Player player) -> {
                                        return new LootTableEditingMenu(containerId, inventory, recipe_key);
                                    },
                                    Component.translatable("live_edit.commands.loot_table.menu_title", recipe_key.toString())
                                ));

                            } else {
                                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
                                    (int containerId, Inventory inventory, Player player) -> {
                                        return new RecipeEditingMenu(containerId, inventory, type, recipe_key);
                                    },
                                    Component.translatable("live_edit.commands.recipe.menu_title", recipe_key.toString())
                                ));
                            }
                        }
                        return 0;
                    })
                )
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> testRecipesCommand() {
        return Commands.literal("test")
            .then(Commands.argument("type", new RecipeTypeArgument(true))
                .executes(ctx -> {
                    RecipeType type = RecipeTypeArgument.getRecipeType(ctx, "type");
                    List<ResourceLocation> failures;
                    if (type == RecipeType.ALL) {
                        failures = new ArrayList<>();
                        for (RecipeType actual_type : RecipeManager.instance.manipulators.keySet()) {
                            failures.addAll(testAllRecipesOfType(actual_type, RecipeManager.instance.manipulators.get(actual_type)));
                        }
                    } else {
                        failures = testAllRecipesOfType(type, RecipeManager.instance.manipulators.get(type));
                    }
                    String list = failures.stream().map(key -> "\n\u2022 " + key.toString())
                        .collect(Collectors.joining());

                    ctx.getSource().sendSuccess(Component.translatable("live_edit.commands.recipe.test", list), false);
                    return 1;
                })
            );
    }

    private static <R> List<ResourceLocation> testAllRecipesOfType(RecipeType type, IRecipeManipulator<ResourceLocation, R, ?> manipulator) {
        Collection<R> currentRecipes = manipulator.getCurrentRecipes();
        List<ResourceLocation> failures = new ArrayList<>();
        if (type == RecipeType.LOOT_TABLE) {
            for (R table : currentRecipes) {
                try {
                    LootTableConverter.convertLootTable((LootTable) table);
                } catch (UnsupportedOperationException e) {
                    failures.add(manipulator.getKey(table));
                }
            }

        } else {
            for (R recipe : currentRecipes) {
                if (manipulator.encodeRecipe(recipe) == null) {
                    failures.add(manipulator.getKey(recipe));
                }
            }
        }
        return failures;
    }
}

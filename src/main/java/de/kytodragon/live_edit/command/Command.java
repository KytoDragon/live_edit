package de.kytodragon.live_edit.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.recipe.*;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Command {

    private static final int COMMAND_PERMISSION = 0;

    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        ArgumentTypeInfos.registerByClass(RecipeArgument.class, SingletonArgumentInfo.contextFree(RecipeArgument::new));
        ArgumentTypeInfos.registerByClass(RecipeTypeArgument.class, SingletonArgumentInfo.contextFree(RecipeTypeArgument::new));

        event.getDispatcher().register(
            LiteralArgumentBuilder.<CommandSourceStack>literal("live-edit")
            .requires(cs -> cs.hasPermission(COMMAND_PERMISSION))
            .then(reloadCommand())
            .then(listRecipesCommand(event.getBuildContext()))
            .then(deleteRecipesCommand())
            .then(replaceItemCommand(event.getBuildContext()))
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
            .then(Commands.literal("result")
                .then(Commands.argument("item", new ItemArgument(buildContext))
                    .executes(ctx -> {
                        Item item = ItemArgument.getItem(ctx, "item").getItem();

                        Stream<IRecipeManipulator<ResourceLocation, ?, ?>> manipulators = RecipeManager.instance.manipulators.values().stream().filter(IRecipeManipulator::isRealImplementation);
                        Stream<Pair<RecipeType, ResourceLocation>> matching_recipes = manipulators.flatMap(manipulator -> findItem(manipulator, item));
                        String list = matching_recipes.map(pair -> pair.getLeft().name() + " " + pair.getRight().toString())
                                            .collect(Collectors.joining("\n\u2022 ","\n\u2022 ", ""));
                        ctx.getSource().sendSuccess(Component.translatable("commands.live_edit.list", list), false);
                        return 0;
                    })
                )
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> deleteRecipesCommand() {
        return Commands.literal("delete")
            .then(Commands.argument("type", new RecipeTypeArgument())
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

                        ctx.getSource().sendSuccess(Component.translatable("commands.live_edit.recipe.marked_for_deletion", recipe_key.toString()), false);
                        return 1;
                    })
                )
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> replaceItemCommand(CommandBuildContext buildContext) {
        return Commands.literal("replace")
            .then(Commands.argument("item", new ItemArgument(buildContext))
                .then(Commands.argument("replacement", new ItemArgument(buildContext))
                    .executes(ctx -> {
                        Item item = ItemArgument.getItem(ctx, "item").getItem();
                        Item replacement = ItemArgument.getItem(ctx, "replacement").getItem();
                        RecipeManager.instance.markItemForReplacement(item, replacement);

                        ctx.getSource().sendSuccess(Component.translatable("commands.live_edit.item.marked_for_replacement", item.toString(), replacement.toString()), false);
                        return 1;
                    })
                )
            );
    }

    private static <T> Stream<Pair<RecipeType, ResourceLocation>> findItem(IRecipeManipulator<ResourceLocation, T, ?> manipulator, Item item) {
        return manipulator.getCurrentRecipes().stream().map(manipulator::encodeRecipe).filter(recipe -> {
            if (recipe == null)
                return false;

            if (recipe.result != null) {
                for (MyResult result : recipe.result) {
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
}

package de.kytodragon.live_edit.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.event.RegisterCommandsEvent;

import java.util.Collection;
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
            .then(deleteRecipesCommand(event.getBuildContext()))
            .then(replaceItemCommand(event.getBuildContext()))
        );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reloadCommand() {
        return Commands.literal("reload")
            .executes(ctx -> {
                RecipeManager.instance.manipulateAllRecipesAndReload();
                return 0;
            });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> listRecipesCommand(CommandBuildContext buildContext) {
        return Commands.literal("list")
            .then(Commands.literal("result")
                .then(Commands.argument("item", new ItemArgument(buildContext))
                    .executes(ctx -> {
                        Item item = ItemArgument.getItem(ctx, "item").getItem();

                        net.minecraft.world.item.crafting.RecipeManager recipeManager = ctx.getSource().getServer().getRecipeManager();
                        Stream<?> all_recipes = RecipeManager.instance.manipulators.values().stream().filter(IRecipeManipulator::isRealImplementation)
                                                        .map(IRecipeManipulator::getCurrentRecipes).flatMap(Collection::stream);
                        //Stream<ResourceLocation> matching_recipes = all_recipes.filter(s -> s.getResultItem().is(item)).map(Recipe::getId);
                        /*String list = matching_recipes.map(ResourceLocation::toString)
                                            .collect(Collectors.joining("\n\u2022 ","\n\u2022 ", ""));
                        ctx.getSource().sendSuccess(Component.translatable("commands.live_edit.list", list), false);*/
                        return 0;
                    })
                )
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> deleteRecipesCommand(CommandBuildContext buildContext) {
        return Commands.literal("delete")
            .then(Commands.argument("type", new RecipeTypeArgument())
                .then(Commands.argument("recipe", new RecipeArgument())
                    .executes(ctx -> {
                        RecipeType type = RecipeTypeArgument.getRecipeType(ctx, "type");
                        ResourceLocation recipe_key = RecipeArgument.getRecipe(ctx, type, "recipe");
                        RecipeManager.instance.markRecipeForDeletion(type, recipe_key);

                        ctx.getSource().sendSuccess(Component.translatable("commands.live_edit.recipe.marked_for_deletion", recipe_key.toString()), false);
                        return 0;
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
                        return 0;
                    })
                )
            );
    }
}

package de.kytodragon.live_edit.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.kytodragon.live_edit.recipe.IRecipeManipulator;
import de.kytodragon.live_edit.recipe.RecipeManager;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class RecipeArgument implements ArgumentType<ResourceLocation> {

    private static final Collection<String> EXAMPLES = List.of("minecraft:stone");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_RECIPE = new DynamicCommandExceptionType((name) -> {
        return Component.translatable("recipe.notFound", name);
    });

    public RecipeArgument() {
    }

    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        return ResourceLocation.read(reader);
    }

    public static ResourceLocation getRecipe(CommandContext<CommandSourceStack> ctx, RecipeType type, String name) throws CommandSyntaxException {
        IRecipeManipulator<?, ?, ?> manipulator = RecipeManager.instance.manipulators.get(type);
        ResourceLocation resourcelocation = ctx.getArgument(name, ResourceLocation.class);
        manipulator.getRecipe(resourcelocation).orElseThrow(() -> {
            return ERROR_UNKNOWN_RECIPE.create(resourcelocation);
        });
        return resourcelocation;
    }

    public static ResourceLocation getRecipeOrNull(CommandContext<CommandSourceStack> ctx, RecipeType type, String name) {
        IRecipeManipulator<?, ?, ?> manipulator = RecipeManager.instance.manipulators.get(type);
        ResourceLocation resourcelocation = ctx.getArgument(name, ResourceLocation.class);
        if (manipulator.getRecipe(resourcelocation).isEmpty()) {
            resourcelocation = null;
        }
        return resourcelocation;
    }

    public static void throwError(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        ResourceLocation resourcelocation = ctx.getArgument(name, ResourceLocation.class);
        throw ERROR_UNKNOWN_RECIPE.create(resourcelocation);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> ctx, SuggestionsBuilder builder) {

        RecipeType type = ctx.getArgument("type", RecipeType.class);
        Stream<IRecipeManipulator<?, ?, ?>> manipulators;
        if (type == RecipeType.ALL) {
            manipulators = RecipeManager.instance.manipulators.values().stream();
        } else {
            manipulators = Stream.of(RecipeManager.instance.manipulators.get(type));
        }

        Stream<ResourceLocation> recipes = manipulators.filter(IRecipeManipulator::isRealImplementation).flatMap(RecipeArgument::getRecipes);
        return SharedSuggestionProvider.suggestResource(recipes, builder);
    }

    private static <T> Stream<ResourceLocation> getRecipes(IRecipeManipulator<T, ?, ?> manipulator) {
        return manipulator.getCurrentRecipes().stream().map(manipulator::getKey).filter(Objects::nonNull);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

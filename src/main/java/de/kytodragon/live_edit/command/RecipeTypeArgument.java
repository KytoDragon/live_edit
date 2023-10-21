package de.kytodragon.live_edit.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.kytodragon.live_edit.recipe.RecipeManager;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class RecipeTypeArgument implements ArgumentType<RecipeType> {

    private static final Collection<String> EXAMPLES = List.of(RecipeType.CRAFTING.name());
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_RECIPE_TYPE = new DynamicCommandExceptionType((name) -> {
        return Component.translatable("commands.live_edit.recipe_type_not_found", name);
    });

    public RecipeTypeArgument() {
    }

    public static RecipeType getRecipeType(final CommandContext<?> context, final String name) {
        return context.getArgument(name, RecipeType.class);
    }

    @Override
    public RecipeType parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();

        while(reader.canRead() && Character.isAlphabetic(reader.peek())) {
            reader.skip();
        }

        String key = reader.getString().substring(start, reader.getCursor()).toUpperCase();

        return getRealRecipeTypes().filter(s -> s.name().equals(key)).findAny()
                .orElseThrow(() -> ERROR_UNKNOWN_RECIPE_TYPE.createWithContext(reader, key));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> ctx, SuggestionsBuilder builder) {

        Stream<String> stream = getRealRecipeTypes().map(RecipeType::name);
        return SharedSuggestionProvider.suggest(stream, builder);
    }

    private Stream<RecipeType> getRealRecipeTypes() {
        return RecipeManager.instance.manipulators.entrySet().stream()
                .filter(s -> s.getValue().isRealImplementation()).map(Map.Entry::getKey);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

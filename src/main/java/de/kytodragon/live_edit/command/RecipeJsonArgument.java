package de.kytodragon.live_edit.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

import java.util.Collection;
import java.util.List;

public class RecipeJsonArgument implements ArgumentType<JsonObject> {

    private static final Collection<String> EXAMPLES = List.of(); // TODO
    private static final DynamicCommandExceptionType ERROR_FAILED_TO_PARSE = new DynamicCommandExceptionType((name) -> {
        return Component.translatable("commands.live_edit.recipe.could_not_parse", name.toString());
    });

    @Override
    public JsonObject parse(StringReader reader) throws CommandSyntaxException {
        try {
            JsonObject result = GsonHelper.parse(reader.getRemaining());
            reader.setCursor(reader.getTotalLength());
            return result;
        } catch (RuntimeException e) {
            throw ERROR_FAILED_TO_PARSE.create(e.getMessage());
        }
    }

    public static JsonObject getRecipe(CommandContext<CommandSourceStack> ctx, String parameter_name) {
        return ctx.getArgument(parameter_name, JsonObject.class);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

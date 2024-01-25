package de.kytodragon.live_edit.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import de.kytodragon.live_edit.editing.MyLootTable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;

public class LootTableJsonArgument implements ArgumentType<MyLootTable> {

    private static final Collection<String> EXAMPLES = List.of(); // TODO
    private static final DynamicCommandExceptionType ERROR_FAILED_TO_PARSE = new DynamicCommandExceptionType((name) -> {
        return Component.translatable("commands.live_edit.loot_table.could_not_parse", name.toString());
    });

    @Override
    public MyLootTable parse(StringReader reader) throws CommandSyntaxException {
        try {
            MyLootTable result = MyLootTable.fromJsonString(reader.getRemaining());
            reader.setCursor(reader.getTotalLength());
            return result;
        } catch (RuntimeException e) {
            throw ERROR_FAILED_TO_PARSE.create(e.getMessage());
        }
    }

    public static MyLootTable getLootTable(CommandContext<CommandSourceStack> ctx, String parameter_name) {
        return ctx.getArgument(parameter_name, MyLootTable.class);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

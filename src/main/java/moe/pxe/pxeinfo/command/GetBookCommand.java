package moe.pxe.pxeinfo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import moe.pxe.pxeinfo.Book;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GetBookCommand {

    private static Integer command(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getExecutor() instanceof final Player player)) {
            ctx.getSource().getSender().sendRichMessage("<red><tr:permissions.requires.player>");
            return 0;
        }

        Integer amount = 1;
        try {
            amount = ctx.getArgument("amount", Integer.class);
        } catch (Exception ignored) {}

        ItemStack book = ctx.getArgument("book", Book.class).getItem();
        book.setAmount(amount);
        player.give(book);

        ctx.getSource().getSender().sendMessage(Component.translatable("commands.give.success.single").arguments(
                Component.text(amount),
                book.displayName(),
                player.displayName()
        ));
        return Command.SINGLE_SUCCESS;
    }

    public static LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("get")
                .requires(ctx -> ctx.getSender().hasPermission("info.get") || ctx.getSender().isOp())
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(GetBookCommand::command))
                .executes(GetBookCommand::command)
                .build();
    }

}

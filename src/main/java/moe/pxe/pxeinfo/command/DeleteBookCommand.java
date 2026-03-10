package moe.pxe.pxeinfo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import moe.pxe.pxeinfo.Book;
import moe.pxe.pxeinfo.Books;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class DeleteBookCommand {

    public static LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("del")
                .requires(ctx -> ctx.getSender().hasPermission("info.delete") || ctx.getSender().isOp())
                .executes(ctx -> {
                    String bookName = ctx.getArgument("book", Book.class).getName();
                    Books.deleteBook(bookName);
                    ctx.getSource().getSender().sendRichMessage("Deleted info book <book>", Placeholder.unparsed("book", bookName));
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

}

package moe.pxe.pxeinfo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import moe.pxe.pxeinfo.Book;
import moe.pxe.pxeinfo.Books;
import moe.pxe.pxeinfo.Main;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class DeleteBookCommand {

    public static LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("delete")
                .requires(ctx -> ctx.getSender().hasPermission("info.delete") || ctx.getSender().isOp())
                .executes(ctx -> {
                    Book book = ctx.getArgument("book", Book.class);
                    String bookName = book.getName();

                    if (bookName.equals("toc")) book.setItem(null);
                    else Books.deleteBook(bookName);
                    Main.getInstance().saveBooksConfig();

                    ctx.getSource().getSender().playSound(Main.DELETE_SOUND);
                    ctx.getSource().getSender().sendRichMessage("Deleted info book <book>", Placeholder.unparsed("book", bookName));
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

}

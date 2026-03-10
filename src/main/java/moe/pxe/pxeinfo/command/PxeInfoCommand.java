package moe.pxe.pxeinfo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import moe.pxe.pxeinfo.Book;
import moe.pxe.pxeinfo.Books;
import moe.pxe.pxeinfo.Main;
import moe.pxe.pxeinfo.command.argument.BookArgument;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class PxeInfoCommand {

    public static LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("pxeinfo")
                .then(Commands.literal("reload")
                        .requires(ctx -> ctx.getSender().hasPermission("info.reload") | ctx.getSender().isOp())
                        .executes(ctx -> {
                            Main.getInstance().reloadConfig();
                            Main.getInstance().reloadBooksConfig();
                            ctx.getSource().getSender().sendRichMessage("Reloaded plugin configuration and all books.");
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("motd")
                        .requires(ctx -> ctx.getSender().hasPermission("info.motd") || ctx.getSender().isOp())
                        .then(Commands.argument("book", new BookArgument())
                                .executes(ctx -> {
                                    String bookName = ctx.getArgument("book", Book.class).getName();
                                    Books.setMotdBook(bookName);
                                    Main.getInstance().saveBooksConfig();

                                    ctx.getSource().getSender().playSound(Main.MODIFY_SOUND);
                                    ctx.getSource().getSender().sendRichMessage("Set MOTD book to <book>\n<gray><i>This book will now open whenever players first log in after now.", Placeholder.unparsed("book", bookName));
                                    return Command.SINGLE_SUCCESS;
                                }))
                        .executes(ctx -> {
                            Books.setMotdBook(null);
                            Main.getInstance().saveBooksConfig();

                            ctx.getSource().getSender().playSound(Main.REMOVE_SOUND);
                            ctx.getSource().getSender().sendRichMessage("MOTD book has been cleared");
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }

}

package moe.pxe.pxeinfo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import moe.pxe.pxeinfo.Book;
import moe.pxe.pxeinfo.Books;
import moe.pxe.pxeinfo.Main;
import moe.pxe.pxeinfo.command.argument.BookArgument;
import moe.pxe.pxeinfo.command.book.*;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RootCommand {

    public static LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("info")
                .requires(ctx -> ctx.getSender().hasPermission("info.use") || ctx.getSender().isOp())
                .then(Commands.argument("book", new BookArgument())
                        .then(SetBookCommand.getCommand())
                        .then(DeleteBookCommand.getCommand())
                        .then(GetBookCommand.getCommand())
                        .then(SetDisplayNameCommand.getCommand())
                        .then(SetDescriptionCommand.getCommand())
                        .then(SetPermissionCommand.getCommand())
                        .executes(ctx -> {
                            if (!(ctx.getSource().getExecutor() instanceof final Player player)) {
                                ctx.getSource().getSender().sendRichMessage("<red><tr:permissions.requires.player>");
                                return 0;
                            }

                            final Book book = ctx.getArgument("book", Book.class);
                            book.openBook(player);
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.argument("name", StringArgumentType.word())
                        .requires(ctx -> ctx.getSender().hasPermission("info.create") || ctx.getSender().isOp())
                        .then(Commands.literal("set")
                                .executes(ctx -> {
                                    if (!(ctx.getSource().getExecutor() instanceof final Player player)) {
                                        ctx.getSource().getSender().sendRichMessage("<red><tr:permissions.requires.player>");
                                        return 0;
                                    }

                                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                                    if (!heldItem.getType().equals(Material.WRITTEN_BOOK)) {
                                        ctx.getSource().getSender().sendRichMessage("<red>You must be holding a <tr:item.minecraft.writable_book>");
                                        return 0;
                                    }

                                    Book book = Books.newBook(ctx.getArgument("name", String.class), heldItem);
                                    Main.getInstance().saveBooksConfig();
                                    ctx.getSource().getSender().sendRichMessage("Created new info book <book>", Placeholder.component("book", book.getComponent()));
                                    return Command.SINGLE_SUCCESS;
                                })))
                .executes(ctx -> {
                    if (!(ctx.getSource().getExecutor() instanceof final Player player)) {
                        ctx.getSource().getSender().sendRichMessage("<red><tr:permissions.requires.player>");
                        return 0;
                    }

                    Book toc = Books.getTableOfContents();
                    if (toc == null) {
                        ctx.getSource().getSender().sendRichMessage("<red>Please specify a book to view.");
                        return 0;
                    }
                    toc.openBook(player);
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

}

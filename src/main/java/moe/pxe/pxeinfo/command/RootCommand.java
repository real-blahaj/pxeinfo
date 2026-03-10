package moe.pxe.pxeinfo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import moe.pxe.pxeinfo.Book;
import moe.pxe.pxeinfo.Books;
import moe.pxe.pxeinfo.Main;
import moe.pxe.pxeinfo.command.argument.BookArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class RootCommand {

    private static final MiniMessage MINIMESSAGE = MiniMessage.miniMessage();

    public static int displayTableOfContents(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getExecutor() instanceof final Player player)) {
            ctx.getSource().getSender().sendRichMessage("<red><tr:permissions.requires.player>");
            return 0;
        }

        Collection<Component> bookPages = new ArrayList<>();

        Book mainBook = Books.getBook("main");
        if (mainBook != null && mainBook.getItem().getItemMeta() instanceof final BookMeta meta) bookPages.addAll(meta.pages());

        int idx = -1;
        Component currentPage = Component.empty();
        Book[] books = Arrays.stream(Books.getAllBooks())
                .filter(book -> book.hasPermission(ctx.getSource().getSender()))
                .filter(book -> !book.getName().equals("main"))
                .toArray(Book[]::new);
        if (books.length == 0) {
            currentPage = MINIMESSAGE.deserialize("""
                    <dark_aqua>Table of Contents</dark_aqua>
                    
                    <dark_gray>No info books found!</dark_gray>
                    <gray><i>Either no books have been created or you don't have permission to view any of them.""");
        }
        else for (Book book : books) {
            idx++;
            if (idx % 12 == 0) {
                if (idx > 0) bookPages.add(currentPage);
                currentPage = MINIMESSAGE.deserialize("<dark_aqua>Table of Contents</dark_aqua> <gray>(<warp_count>)</gray>\n", Placeholder.unparsed("warp_count", String.valueOf(books.length)));
            }
            currentPage = currentPage.append(MINIMESSAGE.deserialize("\n<dark_gray>•</dark_gray> <warp>", Placeholder.component("warp", book.getComponent())));
        }
        bookPages.add(currentPage);

        player.openBook(net.kyori.adventure.inventory.Book.book(Component.empty(), Component.empty(), bookPages));
        player.playSound(Main.OPEN_BOOK_SOUND);
        return Command.SINGLE_SUCCESS;
    }

    public static LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("info")
                .requires(ctx -> ctx.getSender().hasPermission("info.use") || ctx.getSender().isOp())
                .then(Commands.argument("book", new BookArgument())
                        .then(SetBookCommand.getCommand())
                        .then(DeleteBookCommand.getCommand())
                        .then(GetBookCommand.getCommand())
                        .then(SetMotdCommand.getCommand())
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
                                    ctx.getSource().getSender().sendRichMessage("Created new info book <book>", Placeholder.component("book", book.getComponent()));
                                    return Command.SINGLE_SUCCESS;
                                })))
                .executes(RootCommand::displayTableOfContents)
                .build();
    }

}

package moe.pxe.pxeinfo;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

public class TableOfContents extends Book {

    private static final MiniMessage MINIMESSAGE = MiniMessage.miniMessage();
    private static final Supplier<FileConfiguration> CONFIG = Main.getInstance()::getConfig;

    public TableOfContents() {
        super("toc");
    }

    @Override
    public void openBook(Player player) {
        if (!CONFIG.get().getBoolean("table-of-contents.enabled")) super.openBook(player);

        Collection<Component> bookPages = new ArrayList<>();

        ItemStack mainBook = this.getItem();
        if (mainBook != null && mainBook.getItemMeta() instanceof final BookMeta meta) bookPages.addAll(meta.pages());

        int idx = -1;
        Component currentPage = Component.empty();
        Book[] books = Arrays.stream(Books.getAllBooks())
                .filter(book -> book.hasPermission(player) && !(book instanceof TableOfContents))
                .toArray(Book[]::new);
        if (books.length == 0) {
            currentPage = MINIMESSAGE.deserialize(CONFIG.get().getString("table-of-contents.empty-page", """
                    <dark_aqua>Table of Contents</dark_aqua>
                    
                    <dark_gray>No info books found!</dark_gray>
                    <gray><i>Either no books have been created or you don't have permission to view any of them."""));
        }
        else for (Book book : books) {
            idx++;
            if (idx % 12 == 0) {
                if (idx > 0) bookPages.add(currentPage);
                currentPage = MINIMESSAGE.deserialize(CONFIG.get().getString("table-of-contents.header", "<dark_aqua>Table of Contents</dark_aqua> <gray>(<book_count>)</gray>"),
                        Placeholder.unparsed("book_count", String.valueOf(books.length)))
                        .appendNewline();
            }
            currentPage = currentPage.append(MINIMESSAGE.deserialize("\n<dark_gray>•</dark_gray> <book>", Placeholder.component("book", book.getComponent())));
        }
        bookPages.add(currentPage);

        player.playSound(Main.OPEN_TOC_SOUND);
        player.openBook(net.kyori.adventure.inventory.Book.book(Component.empty(), Component.empty(), bookPages));
    }
}

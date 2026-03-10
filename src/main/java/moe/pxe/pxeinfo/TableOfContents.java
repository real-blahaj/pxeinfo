package moe.pxe.pxeinfo;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class TableOfContents extends Book {

    private static final MiniMessage MINIMESSAGE = MiniMessage.miniMessage();

    public TableOfContents() {
        super("toc");
    }

    @Override
    public void openBook(Player player) {
        Collection<Component> bookPages = new ArrayList<>();

        ItemStack mainBook = this.getItem();
        if (mainBook != null && mainBook.getItemMeta() instanceof final BookMeta meta) bookPages.addAll(meta.pages());

        int idx = -1;
        Component currentPage = Component.empty();
        Book[] books = Arrays.stream(Books.getAllBooks())
                .filter(book -> book.hasPermission(player) && !book.getName().equals("toc"))
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
                currentPage = MINIMESSAGE.deserialize("<dark_aqua>Table of Contents</dark_aqua> <gray>(<book_count>)</gray>\n", Placeholder.unparsed("book_count", String.valueOf(books.length)));
            }
            currentPage = currentPage.append(MINIMESSAGE.deserialize("\n<dark_gray>•</dark_gray> <book>", Placeholder.component("book", book.getComponent())));
        }
        bookPages.add(currentPage);

        player.playSound(Main.OPEN_BOOK_SOUND);
        player.openBook(net.kyori.adventure.inventory.Book.book(Component.empty(), Component.empty(), bookPages));
    }
}

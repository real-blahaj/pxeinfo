package moe.pxe.pxeinfo;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

public class Books {
    private static final HashMap<String, Book> BOOKS = new HashMap<>();
    private static String motdBook;
    private static Long motdLastUpdated;

    private static final Book tableOfContentsBook = new TableOfContents();

    public static Book getBook(String name) {
        return BOOKS.get(name);
    }

    public static Book newBook(String name, ItemStack item) {
        Book book = new Book(name, item);
        BOOKS.put(name, book);
        return book;
    }

    public static void deleteBook(String name) {
        BOOKS.remove(name);
    }

    public static Book[] getAllBooks() {
        Collection<Book> values = BOOKS.values();
        return values.toArray(new Book[0]);
    }

    private static Book loadBook(Book book, MemorySection section) {
        book.setDisplayName(section.getRichMessage("display-name"));
        book.setDescription(section.getRichMessage("description"));
        book.setPermission(section.getString("permission"));
        book.setItem(section.getItemStack("book"));
        return book;
    }

    public static void loadBooks(FileConfiguration config) {
        ConfigurationSection booksSection = config.getConfigurationSection("books");
        if (booksSection != null) booksSection.getValues(true).forEach((name, obj) -> {
            if (!(obj instanceof MemorySection section)) return;
            Book book = new Book(name);
            if (name.equals("toc")) book = tableOfContentsBook;
            BOOKS.put(name, loadBook(book, section));
        });

        ConfigurationSection motdSection = config.getConfigurationSection("motd");
        if (motdSection != null) {
            motdBook = motdSection.getString("name");
            motdLastUpdated = motdSection.getLong("last-updated");
        }
    }

    private static void saveBook(Book book, FileConfiguration config) {
        String name = book.getName();
        config.setRichMessage("books."+name+".display-name", book.getDisplayName());
        config.setRichMessage("books."+name+".description", book.getDescription());
        config.set("books."+name+".permission", book.getPermission());
        config.set("books."+name+".book", book.getItem());
    }

    public static void flushBooks(FileConfiguration config) {
        try {
            config.loadFromString("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Book book : getAllBooks()) saveBook(book, config);
        if (motdBook != null) {
            config.set("motd.name", motdBook);
            config.set("motd.last-updated", motdLastUpdated);
        }
    }

    public static Book getMotdBook() {
        return getBook(motdBook);
    }

    public static void setMotdBook(String motdBook) {
        Books.motdBook = motdBook;
        motdLastUpdated = new Date().getTime();
    }

    public static Long getMotdLastUpdated() {
        return motdLastUpdated;
    }

    public static Book getTableOfContents() {
        return tableOfContentsBook;
    }
}

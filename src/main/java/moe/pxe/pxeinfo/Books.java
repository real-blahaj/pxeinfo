package moe.pxe.pxeinfo;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Books {
    private static final HashMap<String, Book> BOOKS = new HashMap<>();
    private static String motdBook;
    private static Long motdLastUpdated;

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
        return BOOKS.values().toArray(new Book[0]);
    }

    public static void loadBooks(FileConfiguration config) {
        ConfigurationSection booksSection = config.getConfigurationSection("books");
        if (booksSection != null) booksSection.getValues(true).forEach((name, obj) -> {
            if (!(obj instanceof MemorySection section)) return;
            if (!section.contains("book")) return;
            Component displayName = section.getRichMessage("display-name");
            Component description = section.getRichMessage("description");
            String permission = section.getString("permission");
            ItemStack item = section.getItemStack("book");

            BOOKS.put(name, new Book(name, displayName, description, permission, item));
        });

        ConfigurationSection motdSection = config.getConfigurationSection("motd");
        if (motdSection != null) {
            motdBook = motdSection.getString("motd.name");
            motdLastUpdated = motdSection.getLong("motd.last-updated");
        }
    }

    public static void flushBooks(FileConfiguration config) {
        try {
            config.loadFromString("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, Book> book : BOOKS.entrySet()) {
            config.setRichMessage("books."+book.getKey()+".display-name", book.getValue().getDisplayName());
            config.setRichMessage("books."+book.getKey()+".description", book.getValue().getDescription());
            config.set("books."+book.getKey()+".book", book.getValue().getItem());
        }
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
}

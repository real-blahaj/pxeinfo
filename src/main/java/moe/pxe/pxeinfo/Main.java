package moe.pxe.pxeinfo;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import moe.pxe.pxeinfo.command.PxeInfoCommand;
import moe.pxe.pxeinfo.command.RootCommand;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class Main extends JavaPlugin {

    private static Main INSTANCE;

    public static final Sound OPEN_BOOK_SOUND = Sound.sound(Key.key("item.book.page_turn"), Sound.Source.MASTER, 1f, 1f);
    public static final Sound OPEN_TOC_SOUND = Sound.sound(Key.key("block.chiseled_bookshelf.pickup.enchanted"), Sound.Source.MASTER, 1f, 1f);
    public static final Sound GET_SOUND = Sound.sound(Key.key("entity.item.pickup"), Sound.Source.MASTER, 0.65f, 1f);
    public static final Sound MODIFY_SOUND = Sound.sound(Key.key("entity.item_frame.place"), Sound.Source.MASTER, 0.75f, 1.25f);
    public static final Sound REMOVE_SOUND = Sound.sound(Key.key("entity.item_frame.remove_item"), Sound.Source.MASTER, 0.75f, 0.793701f);
    public static final Sound DELETE_SOUND = Sound.sound(Key.key("block.fire.extinguish"), Sound.Source.MASTER, 0.75f, 2f);

    private FileConfiguration booksConfig;
    private final File booksConfigFile = new File(getDataFolder(), "books.yml");

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        saveDefaultConfig();
        createBooksConfig();
        reloadBooksConfig();

        getServer().getPluginManager().registerEvents(new Events(), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands ->
                {
                    commands.registrar().register(RootCommand.getCommand(), "Displays help in the form of informational books", List.of("book"));
                    commands.registrar().register(PxeInfoCommand.getCommand(), "Manage the pxeinfo plugin");
                }
        );
    }

    @Override
    public void onDisable() {
        saveBooksConfig();
        saveConfig();
    }

    public static Main getInstance() {
        return INSTANCE;
    }

    public void reloadBooksConfig() {
        if (booksConfig == null) booksConfig = new YamlConfiguration();
        try {
            booksConfig.load(booksConfigFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Books.loadBooks(booksConfig);
    }

    public void saveBooksConfig() {
        Books.flushBooks(booksConfig);
        try {
            booksConfig.save(booksConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createBooksConfig() {
        if (!booksConfigFile.exists()) {
            booksConfigFile.getParentFile().mkdirs();
            saveResource("books.yml", false);
        }
    }
}

package moe.pxe.pxeinfo;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Date;

public class Events implements Listener {
    private static final NamespacedKey MOTD_SEEN_KEY = new NamespacedKey(Main.getInstance(), "motd_seen");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Book motdBook = Books.getMotdBook();
        if (motdBook == null) return;
        PersistentDataContainer container = event.getPlayer().getPersistentDataContainer();
        Long motdSeenTimestamp = container.get(MOTD_SEEN_KEY, PersistentDataType.LONG);
        if (motdSeenTimestamp != null && motdSeenTimestamp > Books.getMotdLastUpdated()) return;
        motdBook.openBook(event.getPlayer());
        container.set(MOTD_SEEN_KEY, PersistentDataType.LONG, new Date().getTime());
    }
}

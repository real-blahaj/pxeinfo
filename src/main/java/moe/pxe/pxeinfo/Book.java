package moe.pxe.pxeinfo;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Book {
    private final String name;
    private Component displayName;
    private Component description;
    private String permission;
    private ItemStack item;

    protected Book(String name, Component displayName, Component description, String permission, ItemStack item) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.permission = permission;
        this.item = item;
    }

    public Book(String name, ItemStack item) {
        this.name = name;
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public Component getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setDescription(Component description) {
        this.description = description;
    }

    public void setDisplayName(Component displayName) {
        this.displayName = displayName;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public Component getComponent() {
        Component hover = displayName != null ? displayName.append(Component.text(" ("+name+")", TextColor.color(0x808080))) : Component.text(name);
        if (description != null) hover = hover.appendNewline().append(description.color(NamedTextColor.GRAY));
        hover = hover.append(Component.text("\nClick to view →").color(NamedTextColor.DARK_GRAY));

        return (displayName != null ? displayName : Component.text(name)).hoverEvent(hover).clickEvent(ClickEvent.runCommand("/info "+name));
    }

    public boolean hasPermission(CommandSender player) {
        return permission == null || player.isOp() || player.hasPermission(permission);
    }

    public void openBook(Player player) {
        player.playSound(Main.OPEN_BOOK_SOUND);
        player.openBook(item);
    }
}

package moe.pxe.pxeinfo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import moe.pxe.pxeinfo.Book;
import moe.pxe.pxeinfo.Main;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetBookCommand {

    public static LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("set")
                .requires(ctx -> ctx.getSender().hasPermission("info.set") || ctx.getSender().isOp())
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

                    Book book = ctx.getArgument("book", Book.class);
                    book.setItem(heldItem);

                    ctx.getSource().getSender().sendRichMessage("Set info book <book> to held item", Placeholder.component("book", book.getComponent()));
                    ctx.getSource().getSender().playSound(Main.MODIFY_SOUND);
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

}

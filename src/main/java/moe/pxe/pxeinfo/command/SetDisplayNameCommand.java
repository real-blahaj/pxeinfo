package moe.pxe.pxeinfo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import moe.pxe.pxeinfo.Book;
import moe.pxe.pxeinfo.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class SetDisplayNameCommand {

    private static final MiniMessage MINIMESSAGE = MiniMessage.miniMessage();

    public static LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("displayname")
                .requires(ctx -> ctx.getSender().hasPermission("info.displayname") || ctx.getSender().isOp())
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .suggests((context, builder) -> {
                            Component displayName = context.getArgument("book", Book.class).getDisplayName();
                            if (displayName != null) builder.suggest(MINIMESSAGE.serialize(displayName));
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            Book book = ctx.getArgument("book", Book.class);
                            Component name = MINIMESSAGE.deserialize(ctx.getArgument("name", String.class));

                            book.setDisplayName(name);
                            Main.getInstance().saveBooksConfig();
                            ctx.getSource().getSender().sendRichMessage("Set display name of info book to <name>", Placeholder.component("name", name));
                            ctx.getSource().getSender().playSound(Main.MODIFY_SOUND);
                            return Command.SINGLE_SUCCESS;
                        }))
                .executes(ctx -> {
                    Book book = ctx.getArgument("book", Book.class);

                    book.setDisplayName(null);
                    Main.getInstance().saveBooksConfig();

                    ctx.getSource().getSender().playSound(Main.REMOVE_SOUND);
                    ctx.getSource().getSender().sendRichMessage("Removed display name from <book>", Placeholder.component("book", book.getComponent()));
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

}

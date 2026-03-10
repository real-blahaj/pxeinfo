package moe.pxe.pxeinfo.command.book;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import moe.pxe.pxeinfo.Book;
import moe.pxe.pxeinfo.Main;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class SetPermissionCommand {

    public static LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("permission")
                .requires(ctx -> ctx.getSender().hasPermission("info.permission") || ctx.getSender().isOp())
                .then(Commands.argument("permission", StringArgumentType.greedyString())
                        .suggests((context, builder) -> {
                            String permission = context.getArgument("book", Book.class).getPermission();
                            if (permission != null) builder.suggest(permission);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            Book book = ctx.getArgument("book", Book.class);
                            String permission = ctx.getArgument("permission", String.class);

                            book.setPermission(permission);
                            Main.getInstance().saveBooksConfig();
                            ctx.getSource().getSender().sendRichMessage("Set required permission of info book to <permission>", Placeholder.unparsed("permission", permission));
                            ctx.getSource().getSender().playSound(Main.MODIFY_SOUND);
                            return Command.SINGLE_SUCCESS;
                        }))
                .executes(ctx -> {
                    Book book = ctx.getArgument("book", Book.class);

                    book.setPermission(null);
                    Main.getInstance().saveBooksConfig();

                    ctx.getSource().getSender().playSound(Main.REMOVE_SOUND);
                    ctx.getSource().getSender().sendRichMessage("Removed required permission from <book>", Placeholder.component("book", book.getComponent()));
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

}

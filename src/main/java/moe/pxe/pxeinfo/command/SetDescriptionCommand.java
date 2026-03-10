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

public class SetDescriptionCommand {

    private static final MiniMessage MINIMESSAGE = MiniMessage.miniMessage();

    public static LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("description")
                .requires(ctx -> ctx.getSender().hasPermission("info.description") || ctx.getSender().isOp())
                .then(Commands.argument("description", StringArgumentType.greedyString())
                        .suggests((context, builder) -> {
                            Component description = context.getArgument("book", Book.class).getDescription();
                            if (description != null) builder.suggest(MINIMESSAGE.serialize(description));
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            Book book = ctx.getArgument("book", Book.class);
                            Component description = MINIMESSAGE.deserialize(ctx.getArgument("description", String.class));

                            book.setDescription(description);
                            Main.getInstance().saveBooksConfig();
                            ctx.getSource().getSender().sendRichMessage("Set description of info book to <description>", Placeholder.component("description", description));
                            ctx.getSource().getSender().playSound(Main.MODIFY_SOUND);
                            return Command.SINGLE_SUCCESS;
                        }))
                .executes(ctx -> {
                    Book book = ctx.getArgument("book", Book.class);

                    book.setDescription(null);
                    Main.getInstance().saveBooksConfig();

                    ctx.getSource().getSender().playSound(Main.REMOVE_SOUND);
                    ctx.getSource().getSender().sendRichMessage("Removed description from <book>", Placeholder.component("book", book.getComponent()));
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

}

package moe.pxe.pxeinfo.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import moe.pxe.pxeinfo.Book;
import moe.pxe.pxeinfo.Books;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class BookArgument implements CustomArgumentType.Converted<Book, String> {

    private static final SimpleCommandExceptionType ERROR_BAD_SOURCE = new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(Component.text("The source needs to be a CommandSourceStack")));

    private static final DynamicCommandExceptionType ERROR_NOT_FOUND = new DynamicCommandExceptionType(name ->
            MessageComponentSerializer.message().serialize(Component.text(name + "is not an info book"))
    );

    @Override
    public Book convert(String nativeType) throws CommandSyntaxException {
        final Book book = Books.getBook(nativeType.toLowerCase(Locale.ROOT));
        if (book == null) throw ERROR_NOT_FOUND.create(nativeType);
        return book;
    }

    @Override
    public <S> Book convert(String nativeType, S source) throws CommandSyntaxException {
        if (!(source instanceof CommandSourceStack stack)) throw ERROR_BAD_SOURCE.create();
        Book book = convert(nativeType);
        if (!(stack.getSender() instanceof final Player player)) return book;
        if (!book.hasPermission(player)) throw ERROR_NOT_FOUND.create(nativeType);
        return book;
    }

    @Override
    public @NonNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> @NonNull CompletableFuture<Suggestions> listSuggestions(@NonNull CommandContext<S> context, @NonNull SuggestionsBuilder builder) {
        Stream<Book> stream = Arrays.stream(Books.getAllBooks());
        if (context.getSource() instanceof final CommandSourceStack stack) {
            stream = stream.filter(book -> book.hasPermission(stack.getSender()));
        }
        stream.map(Book::getName)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }
}

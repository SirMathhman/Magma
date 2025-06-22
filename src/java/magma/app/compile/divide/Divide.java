package magma.app.compile.divide;

import magma.api.list.ListLike;
import magma.api.list.ListLikes;

public class Divide {
    private Divide() {
    }

    public static ListLike<String> divide(final CharSequence input) {
        final var segments = ListLikes.<String>empty();
        final var buffer = new StringBuilder();
        final var depth = 0;
        var current = (DivideState) new MutableDivideState(segments, buffer, depth);
        final var length = input.length();
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = Divide.fold(current, c);
        }

        return current.advance()
                .toList();
    }

    private static DivideState fold(final DivideState current, final char c) {
        final var appended = current.append(c);
        if (';' == c && appended.isLevel())
            return appended.advance();

        if ('{' == c)
            return appended.enter();

        if ('}' == c)
            return appended.exit();

        return appended;
    }
}
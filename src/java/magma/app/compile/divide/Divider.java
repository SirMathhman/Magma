package magma.app.compile.divide;

import magma.api.collect.list.ListLike;

public class Divider {
    private Divider() {
    }

    public static ListLike<String> divide(final CharSequence input) {
        final DivideState state = new MutableDivideState();
        final var length = input.length();
        var current = state;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = Divider.fold(current, c);
        }

        return current.advance()
                .segments();
    }

    private static DivideState fold(final DivideState state, final char c) {
        final var appended = state.append(c);
        if (';' == c)
            return appended.advance();
        return appended;
    }
}

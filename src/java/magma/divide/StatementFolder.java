package magma.divide;

public class StatementFolder implements Folder {
    @Override
    public State fold(final State state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel())
            return appended.advance();
        if ('}' == c && appended.isShallow())
            return appended.exit()
                    .advance();
        if ('{' == c)
            return appended.enter();
        if ('}' == c)
            return appended.exit();
        return appended;
    }
}
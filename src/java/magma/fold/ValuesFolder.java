package magma.fold;

import magma.State;

public class ValuesFolder implements Folder {
    @Override
    public State fold(final State state, final char c) {
        if (',' == c)
            return state.advance();
        return state.append(c);
    }
}

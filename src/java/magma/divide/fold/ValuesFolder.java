package magma.divide.fold;

import magma.divide.State;

public class ValuesFolder implements Folder {
    @Override
    public State fold(final State state, final char c) {
        if (',' == c)
            return state.advance();
        return state.append(c);
    }
}

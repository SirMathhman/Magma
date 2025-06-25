package magma.fold;

import magma.State;

public interface Folder {
    State fold(State state, char c);
}

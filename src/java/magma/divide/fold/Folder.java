package magma.divide.fold;

import magma.divide.State;

public interface Folder {
    State fold(State state, char c);
}

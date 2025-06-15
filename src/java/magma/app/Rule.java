package magma.app;

import magma.app.maybe.MaybeNode;

public interface Rule {
    MaybeNode lex(String input);
}

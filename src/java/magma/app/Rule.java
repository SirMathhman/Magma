package magma.app;

import magma.app.maybe.MaybeNode;
import magma.app.maybe.MaybeString;

public interface Rule {
    MaybeString generate(Node node);

    MaybeNode lex(String input);
}

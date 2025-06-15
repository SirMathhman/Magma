package magma.app;

import magma.app.maybe.MaybeNode;

public interface Rule<Node, Generated> {
    Generated generate(Node node);

    MaybeNode lex(String input);
}

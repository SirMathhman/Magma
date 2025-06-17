package magma.app.compile.rule;

import magma.app.compile.node.Node;

public interface Rule<Lex, Generate> {
    Lex lex(String input);

    Generate generate(Node node);
}

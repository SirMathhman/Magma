package magma.app.compile.rule;

public interface Rule<Node, Lex, Generate> {
    Lex lex(String input);

    Generate generate(Node node);
}

package magma.app;

public interface Rule<Node, Lex, Generate> {
    Generate generate(Node node);

    Lex lex(String input);
}

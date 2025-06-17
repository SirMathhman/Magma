package magma.app.compile;

public interface Rule<Node, Lex, Generate> {
    Lex lex(String input);

    Generate generate(Node node);
}

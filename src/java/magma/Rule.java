package magma;

public interface Rule {
    OptionalLike<Node> lex(String input);

    OptionalLike<String> generate(Node node);
}

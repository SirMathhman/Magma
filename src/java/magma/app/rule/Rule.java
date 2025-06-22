package magma.app.rule;

public interface Rule<Node, NodeResult, StringResult> {
    NodeResult lex(String input);

    StringResult generate(Node node);
}

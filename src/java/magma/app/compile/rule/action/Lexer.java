package magma.app.compile.rule.action;

public interface Lexer<Node, NodeResult> {
    NodeResult lex(String input);
}

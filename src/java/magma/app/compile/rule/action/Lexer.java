package magma.app.compile.rule.action;

public interface Lexer<NodeResult> {
    NodeResult lex(String input);
}

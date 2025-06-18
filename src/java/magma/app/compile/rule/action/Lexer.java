package magma.app.compile.rule.action;

import java.util.Optional;

public interface Lexer<Node> {
    Optional<Node> lex(String input);
}

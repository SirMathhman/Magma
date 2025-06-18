package magma.app.compile.rule;

import java.util.Optional;

public interface Lexer<Node> {
    Optional<Node> lex(String input);
}

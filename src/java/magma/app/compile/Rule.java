package magma.app.compile;

public interface Rule {
    StringResult<CompileError> generate(Node node);

    NodeResult<Node, CompileError> lex(String input);
}

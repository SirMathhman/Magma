package magma.app.compile;

public interface Rule {
    CompileResult<Node> parse(String input);

    CompileResult<String> generate(Node node);
}

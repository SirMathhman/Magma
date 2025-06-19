package magma.app.compile.rule.action;

public interface Generator<Node, StringResult> {
    StringResult generate(Node node);
}
package magma.app.compile.rule;

import magma.app.compile.CompileError;
import magma.app.compile.Node;
import magma.app.compile.NodeResult;
import magma.app.compile.Rule;
import magma.app.compile.StringResult;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.NodeResults;

public record StringRule(String key) implements Rule {
    @Override
    public StringResult<CompileError> generate(Node node) {
        return node.findString(this.key);
    }

    @Override
    public NodeResult<Node, CompileError> lex(String input) {
        return NodeResults.createFromValue(new MapNode().withString(this.key, input));
    }
}
package magma.app.compile.rule;

import magma.app.compile.CompileError;
import magma.app.compile.Node;
import magma.app.compile.Rule;
import magma.app.compile.NodeResult;
import magma.app.compile.node.NodeResults;
import magma.app.compile.StringResult;
import magma.app.compile.string.StringResults;
import magma.app.compile.node.MapNode;

public class EmptyRule implements Rule<Node, NodeResult<Node, CompileError>, StringResult<CompileError>> {
    @Override
    public StringResult<CompileError> generate(Node node) {
        return StringResults.createFromValue("");
    }

    @Override
    public NodeResult<Node, CompileError> lex(String input) {
        return NodeResults.createFromValue(new MapNode());
    }
}

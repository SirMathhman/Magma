package magma.app.rule;

import magma.app.CompileError;
import magma.app.Node;
import magma.app.Rule;
import magma.app.maybe.NodeResult;
import magma.app.maybe.NodeResults;
import magma.app.maybe.StringResult;
import magma.app.maybe.StringResults;
import magma.app.node.MapNode;

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

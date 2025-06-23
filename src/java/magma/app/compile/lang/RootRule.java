package magma.app.compile.lang;

import magma.app.compile.node.DisplayNode;
import magma.app.compile.node.StringNode;
import magma.app.compile.node.TypedNode;
import magma.app.compile.node.result.NodeResult;
import magma.app.compile.rule.Rule;
import magma.app.compile.string.StringResult;

public interface RootRule<Node extends DisplayNode & StringNode<Node> & TypedNode<Node>, Error> {
    Rule<Node, NodeResult<Node, Error>, StringResult<Error>> create();
}

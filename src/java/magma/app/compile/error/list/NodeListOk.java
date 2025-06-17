package magma.app.compile.error.list;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.node.NodeOk;
import magma.app.compile.error.node.NodeResult;
import magma.app.compile.node.Node;
import magma.app.compile.rule.NodeFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record NodeListOk(List<Node> node) implements NodeListResult<Node, NodeResult<Node, FormattedError>> {
    public NodeListOk() {
        this(new ArrayList<>());
    }

    @Override
    public NodeListResult<Node, NodeResult<Node, FormattedError>> add(Supplier<NodeResult<Node, FormattedError>> action) {
        return switch (action.get()
                .appendTo(this.node)) {
            case Err<List<Node>, FormattedError>(var error) -> new NodeListErr(error);
            case Ok<List<Node>, FormattedError>(var value) -> new NodeListOk(value);
        };
    }

    @Override
    public NodeResult<Node, FormattedError> toNode(NodeFactory<Node> factory, String key) {
        return new NodeOk(factory.create()
                .withNodeList(key, this.node));
    }
}

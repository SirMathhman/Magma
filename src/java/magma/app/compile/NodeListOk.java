package magma.app.compile;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class NodeListOk<Node extends NodeWithNodeLists<Node> & MergeNode<Node> & TypeNode<Node>, Error> implements NodeListResult<Node, NodeResult<Node, Error>> {
    private final List<Node> node;

    public NodeListOk(List<Node> node) {
        this.node = node;
    }

    public NodeListOk() {
        this(new ArrayList<>());
    }

    public static <Node extends MergeNode<Node> & TypeNode<Node>, Error> Result<List<Node>, Error> appendTo(NodeResult<Node, Error> result, List<Node> list) {
        return switch (result) {
            case NodeOk<Node, Error>(var value) -> {
                list.add(value);
                yield new Ok<>(list);
            }
            case NodeErr(var error) -> new Err<>(error);
        };
    }

    @Override
    public NodeListResult<Node, NodeResult<Node, Error>> add(Supplier<NodeResult<Node, Error>> action) {
        return switch (appendTo(action.get(), this.node)) {
            case Err<List<Node>, Error>(var error) -> new NodeListErr<>(error);
            case Ok<List<Node>, Error>(var value) -> new NodeListOk<>(value);
        };
    }

    @Override
    public NodeResult<Node, Error> toNode(NodeFactory<Node> factory, String key) {
        return new NodeOk<>(factory.create()
                .withNodeList(key, this.node));
    }
}

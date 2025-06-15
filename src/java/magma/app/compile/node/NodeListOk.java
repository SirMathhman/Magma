package magma.app.compile.node;

import magma.app.compile.AttachableToNodeListResult;
import magma.app.compile.CompileError;
import magma.app.compile.Node;
import magma.app.compile.NodeListResult;
import magma.app.compile.NodeResult;
import magma.app.compile.StringResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NodeListOk implements NodeListResult<Node, CompileError, NodeResult<Node, CompileError>> {
    private final List<Node> nodes;

    public NodeListOk() {
        this(new ArrayList<>());
    }

    public NodeListOk(List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public NodeListResult<Node, CompileError, NodeResult<Node, CompileError>> add(AttachableToNodeListResult<Node, CompileError> node) {
        return node.attachTo(this.nodes);
    }

    @Override
    public NodeListResult<Node, CompileError, NodeResult<Node, CompileError>> transform(Function<List<Node>, List<Node>> mapper) {
        return new NodeListOk(mapper.apply(this.nodes));
    }

    @Override
    public StringResult<CompileError> generate(Function<List<Node>, StringResult<CompileError>> generator) {
        return generator.apply(this.nodes);
    }

    @Override
    public NodeResult<Node, CompileError> toNode(String key) {
        return NodeResults.createFromValue(new MapNode().withNodeList(key, this.nodes));
    }
}

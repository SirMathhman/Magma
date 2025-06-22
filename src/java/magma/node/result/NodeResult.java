package magma.node.result;

public sealed interface NodeResult<Node> extends Matching<Node>, Mapping<Node, NodeResult<Node>> permits NodeOk,
        NodeErr {
}
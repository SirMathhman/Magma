package magma.node.result;

public sealed interface NodeResult<Node, Error> extends Matching<Node, Error>,
        Mapping<Node, NodeResult<Node, Error>> permits NodeOk,
        NodeErr {
}
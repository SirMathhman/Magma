package magma.app.compile;

import magma.api.Tuple;

import java.util.ArrayList;

public class VisitingModifyingStage implements ModifyingStage {
    private final Visitor visitor;

    public VisitingModifyingStage(Visitor visitor) {
        this.visitor = visitor;
    }

    private Tuple<Node, Integer> modifyNodeLists(Node node, Integer depth) {
        var withNodeLists = new Tuple<>(node, depth);
        for (var tuple : node.streamNodeLists().toList()) {
            var key = tuple.left();
            var oldValues = tuple.right();
            var newValues = new ArrayList<Node>();
            var current = depth;
            for (Node oldValue : oldValues) {
                var newValue = modify(oldValue, depth);
                newValues.add(newValue.left());
                current = newValue.right();
            }

            withNodeLists = new Tuple<>(withNodeLists.left().withNodeList(key, newValues), current);
        }
        return withNodeLists;
    }

    private Tuple<Node, Integer> modifyNodes(Node node, int state) {
        var withNodes = new Tuple<>(node, state);
        for (var tuple : node.streamNodes().toList()) {
            var key = tuple.left();
            var value = tuple.right();
            var newValue = modify(value, withNodes.right());
            var newNode = withNodes.left().withNode(key, newValue.left());
            withNodes = new Tuple<>(newNode, newValue.right());
        }
        return withNodes;
    }

    @Override
    public Tuple<Node, Integer> modify(Node node, int depth) {
        var preVisited = visitor.preVisit(node, depth).orElse(new Tuple<>(node, depth));
        var withNodes = modifyNodes(preVisited.left(), preVisited.right());
        var withNodeLists = modifyNodeLists(withNodes.left(), withNodes.right());

        var left = withNodeLists.left();
        var right = withNodeLists.right();
        return visitor.postVisit(left, right).orElse(new Tuple<>(left, right));
    }
}
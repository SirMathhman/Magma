package magma.app.compile.pass;

import magma.api.Tuple;
import magma.app.compile.Node;
import magma.app.compile.Passer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VisitingModifyingStage implements ModifyingStage {
    private final Passer passer;

    public VisitingModifyingStage(Passer passer) {
        this.passer = passer;
    }

    private Tuple<Node, Integer> modifyNodeLists(Node node, Integer depth) {
        var withNodeLists = new Tuple<>(node, depth);
        Iterator<Tuple<String, List<Node>>> iter = node.streamNodeLists().toList().iterator();
        while (iter.hasNext()) {
            var tuple = iter.next();
            var key = tuple.left();
            var oldValues = tuple.right();
            var newValues = new ArrayList<Node>();
            var current = depth;
            Iterator<Node> iterator = oldValues.iterator();
            while (iterator.hasNext()) {
                Node oldValue = iterator.next();
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
        Iterator<Tuple<String, Node>> iterator = node.streamNodes().toList().iterator();
        while (iterator.hasNext()) {
            var tuple = iterator.next();
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
        var preVisited = passer.preVisit(node, depth).orElse(new Tuple<>(node, depth));
        var withNodes = modifyNodes(preVisited.left(), preVisited.right());
        var withNodeLists = modifyNodeLists(withNodes.left(), withNodes.right());

        var left = withNodeLists.left();
        var right = withNodeLists.right();
        return passer.postVisit(left, right).orElse(new Tuple<>(left, right));
    }
}
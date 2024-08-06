package magma.app.compile;

import magma.api.Tuple;
import magma.app.compile.lang.common.Blocks;

import java.util.ArrayList;
import java.util.Optional;

import static magma.app.compile.lang.common.Blocks.CHILDREN;

public class Formatter implements Visitor {
    @Override
    public Optional<Tuple<Node, Integer>> preVisit(Node node, int state) {
        if (node.is(Blocks.BLOCK)) {
            return Optional.of(new Tuple<>(node, state + 1));
        }

        return Optional.empty();
    }

    @Override
    public Optional<Tuple<Node, Integer>> postVisit(Node node, int state) {
        if (node.is(Blocks.BLOCK)) {
            var oldChildren = node.findNodeList(CHILDREN).orElseThrow();
            var newChildren = new ArrayList<Node>();
            for (int i = 0; i < oldChildren.size(); i++) {
                var newChild = oldChildren.get(i);
                if (i == 0 && state == 0) {
                    newChildren.add(newChild);
                } else {
                    var indent = state < 0 ? "\n" : "\n" + "\t".repeat(state);
                    newChildren.add(newChild.withString(Blocks.BEFORE_CHILD, indent));
                }
            }

            var blockIndent = state <= 0 ? "" : "\t".repeat(state - 1);
            return Optional.of(new Tuple<>(node
                    .withNodeList(CHILDREN, newChildren)
                    .withString(Blocks.AFTER_CHILDREN, "\n" + blockIndent), state - 1));
        }

        return Optional.empty();
    }
}

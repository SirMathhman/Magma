package magma.app.compile.pass;

import magma.api.Tuple;
import magma.app.compile.Node;
import magma.app.compile.Passer;
import magma.app.compile.lang.common.Blocks;
import magma.app.compile.lang.magma.Functions;
import magma.app.compile.lang.magma.MagmaDefinition;
import magma.app.compile.lang.magma.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static magma.app.compile.lang.common.Modifiers.MODIFIERS;
import static magma.app.compile.lang.common.Blocks.CHILDREN;

public class MainPasser implements Passer {
    private static List<Node> flattenRoot(List<Node> oldChildren) {
        var newChildren = new ArrayList<Node>();
        Iterator<Node> iterator = oldChildren.iterator();
        while (iterator.hasNext()) {
            Node oldChild = iterator.next();
            var newChildrenSlice = flattenObjects(oldChild)
                    .orElseGet(() -> Collections.singletonList(oldChild));

            newChildren.addAll(newChildrenSlice);
        }
        return newChildren;
    }

    private static Optional<List<Node>> flattenObjects(Node oldChild) {
        if (!oldChild.is(Objects.OBJECT)) return Optional.empty();

        var value = oldChild.findNode(Objects.VALUE).orElseThrow();
        var objectChildren = value.findNodeList(CHILDREN).orElseThrow();

        var maybeMainChildren = Optional.<Tuple<Integer, List<Node>>>empty();
        int i = 0;
        while (i < objectChildren.size()) {
            Node child = objectChildren.get(i);
            if (child.is(Functions.FUNCTION)) {
                var definition = child.findNode(MagmaDefinition.DEFINITION).orElseThrow();
                var modifiers = definition.findStringList(MODIFIERS).orElse(Collections.emptyList());
                var name = definition.findString(MagmaDefinition.NAME).orElseThrow();

                if (modifiers.contains("public") && name.equals("main")) {
                    var mainChildren = child.findNode(Functions.VALUE)
                            .orElseThrow()
                            .findNodeList(CHILDREN)
                            .orElseThrow();

                    maybeMainChildren = Optional.of(new Tuple<>(i, mainChildren));
                    break;
                }
            }
            i++;
        }

        if (maybeMainChildren.isPresent()) {
            var mainChildren = maybeMainChildren.get();
            var left = objectChildren.subList(0, mainChildren.left());
            var right = objectChildren.subList(mainChildren.left() + 1, objectChildren.size());

            var newChildren = new ArrayList<Node>();
            newChildren.addAll(left);
            newChildren.addAll(right);
            newChildren.addAll(mainChildren.right());

            return Optional.of(newChildren);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Tuple<Node, Integer>> postVisit(Node node, int state) {
        if (!node.is(Blocks.BLOCK)) return Optional.empty();
        return Optional.of(new Tuple<>(node.mapNodeList(CHILDREN, MainPasser::flattenRoot), state));
    }
}

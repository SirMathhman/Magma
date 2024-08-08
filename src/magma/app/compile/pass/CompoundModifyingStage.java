package magma.app.compile.pass;

import magma.api.Tuple;
import magma.app.compile.Node;

import java.util.Iterator;
import java.util.List;

public final class CompoundModifyingStage implements ModifyingStage {
    private final List<ModifyingStage> children;

    public CompoundModifyingStage(List<ModifyingStage> children) {
        this.children = children;
    }

    @Override
    public Tuple<Node, Integer> modify(Node node, int depth) {
        var current = new Tuple<>(node, depth);
        Iterator<ModifyingStage> iterator = children.iterator();
        while (iterator.hasNext()) {
            ModifyingStage child = iterator.next();
            current = child.modify(current.left(), current.right());
        }

        return current;
    }
}

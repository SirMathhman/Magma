package magma.app.compile.pass;

import magma.api.Tuple;
import magma.app.compile.Node;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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

    public List<ModifyingStage> children() {
        return children;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CompoundModifyingStage) obj;
        return Objects.equals(this.children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(children);
    }

    @Override
    public String toString() {
        return "CompoundModifyingStage[" +
               "children=" + children + ']';
    }

}

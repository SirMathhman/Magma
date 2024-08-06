package magma.app.compile;

import magma.api.Tuple;

import java.util.List;

public record CompoundModifyingStage(List<ModifyingStage> children) implements ModifyingStage {
    @Override
    public Tuple<Node, Integer> modify(Node node, int depth) {
        var current = new Tuple<>(node, depth);
        for (ModifyingStage child : children) {
            current = child.modify(current.left(), current.right());
        }

        return current;
    }
}

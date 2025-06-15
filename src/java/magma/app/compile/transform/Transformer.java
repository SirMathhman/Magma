package magma.app.compile.transform;

import magma.app.compile.node.CompoundNode;
import magma.app.compile.node.PropertiesCompoundNode;

import java.util.ArrayList;

public class Transformer {
    public static CompoundNode transform(String name, CompoundNode root) {
        final var children = root.nodeLists().find("children").orElse(new ArrayList<>());
        var values = children.stream().map(child -> {
            return transformRootSegment(name, child);
        }).toList();
        return new PropertiesCompoundNode().nodeLists().with("children", values);
    }

    private static CompoundNode transformRootSegment(String name, CompoundNode child) {
        if (child.is("import")) {
            return child.strings().with("source", name);
        } else {
            return child;
        }
    }
}
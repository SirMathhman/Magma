package magma.app.compile;

import magma.app.compile.node.CompoundNode;
import magma.app.compile.node.PropertiesCompoundNode;

import java.util.ArrayList;

public class Transformer {
    public static CompoundNode transform(String name, CompoundNode root) {
        final var children = root.nodeLists().find("children").orElse(new ArrayList<CompoundNode>());
        var node = new PropertiesCompoundNode();
        var values = children.stream().map(child -> child.strings().with("source", name)).toList();
        return node.nodeLists().with("children", values);
    }
}
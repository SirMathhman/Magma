package magma.app.compile.value;

import magma.app.compile.node.Node;

public record ConstructionCaller(String right) implements Node {
    public String generate() {
        return "new " + this.right;
    }
}

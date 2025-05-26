package magma.app.compile.value;

import magma.app.compile.node.Node;

public record StringNode(String value) implements Node {
    public String generate() {
        return "\"" + this.value + "\"";
    }

    public boolean is(String type) {
        return false;
    }
}

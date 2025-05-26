package magma.app.compile.value;

import magma.app.compile.node.Node;

public record ConstructionCaller(String type) implements Node {
    public String generate() {
        return "new " + this.type;
    }

    @Override
    public boolean is(String type) {
        return "construction".equals(type);
    }
}

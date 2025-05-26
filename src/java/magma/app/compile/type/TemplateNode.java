package magma.app.compile.type;

import magma.app.compile.node.Node;

public record TemplateNode(String base, magma.api.collect.list.List<Node> args) implements Node {
    public boolean is(String type) {
        return "template".equals(type);
    }
}

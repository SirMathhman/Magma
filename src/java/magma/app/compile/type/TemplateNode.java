package magma.app.compile.type;

import magma.api.collect.list.Iterable;
import magma.app.compile.merge.Merger;
import magma.app.compile.merge.ValueMerger;
import magma.app.compile.node.Node;

public record TemplateNode(String base, Iterable<String> args) implements Node {
    public String generateNode() {
        return this.base + "<" + Merger.generateAll(this.args, new ValueMerger()) + ">";
    }

    public boolean isFunctional() {
        return false;
    }

    public boolean isVar() {
        return false;
    }

    public String generateBeforeName() {
        return "";
    }

    public String generateSimple() {
        return this.base;
    }

    public boolean is(String type) {
        return "template".equals(type);
    }
}

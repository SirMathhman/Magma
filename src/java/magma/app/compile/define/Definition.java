package magma.app.compile.define;

import magma.api.collect.Joiner;
import magma.api.collect.list.Iterable;
import magma.api.collect.list.List;
import magma.api.text.Strings;
import magma.app.RootCompiler;
import magma.app.TypeCompiler;
import magma.app.compile.node.Node;

public record Definition(
        List<String> annotations,
        List<String> modifiers,
        Iterable<String> typeParams,
        Node type,
        String name
) implements MethodHeader, Node {
    @Override
    public String generateWithAfterName(String afterName) {
        var joinedNodeParams = this.joinNodeParams();
        var joinedModifiers = this.modifiers.iter()
                .map((String value) -> value + " ")
                .collect(new Joiner(""))
                .orElse("");

        return joinedModifiers + TypeCompiler.generateBeforeName(this.type) + this.name + joinedNodeParams + afterName + this.generateNode();
    }

    private String generateNode() {
        if (this.type.is("var")) {
            return "";
        }

        return ": " + TypeCompiler.generateType(this.type);
    }

    private String joinNodeParams() {
        return RootCompiler.joinNodeParams(this.typeParams);
    }

    @Override
    public boolean hasAnnotation(String annotation) {
        return this.annotations.contains(annotation);
    }

    @Override
    public MethodHeader removeModifier(String modifier) {
        return new Definition(this.annotations, this.modifiers.removeNode(modifier), this.typeParams, this.type, this.name);
    }

    public boolean isNamed(String name) {
        return Strings.equalsTo(this.name, name);
    }

    @Override
    public boolean is(String type) {
        return false;
    }
}

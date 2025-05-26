package magma.app.compile.define;

import magma.api.collect.Joiner;
import magma.api.collect.list.Iterable;
import magma.api.collect.list.List;
import magma.api.option.Option;
import magma.api.option.Some;
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
) implements MethodHeader, Parameter {
    public Node findNode() {
        return this.type;
    }

    public String toAssignment() {
        return "\n\t\tthis." + this.name + " = " + this.name + ";";
    }

    @Override
    public String generate() {
        return this.generateWithAfterName("");
    }

    public Option<Definition> asDefinition() {
        return new Some<Definition>(this);
    }

    @Override
    public String generateWithAfterName(String afterName) {
        var joinedNodeParams = this.joinNodeParams();
        var joinedModifiers = this.modifiers.iter()
                .map((String value) -> {
                    return value + " ";
                })
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
}

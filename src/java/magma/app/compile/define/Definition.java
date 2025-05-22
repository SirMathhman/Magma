package magma.app.compile.define;

import jvm.api.collect.list.Lists;
import magma.api.collect.Joiner;
import magma.api.collect.list.Iterable;
import magma.api.collect.list.List;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.text.Strings;
import magma.app.TypeCompiler;
import magma.app.compile.type.Type;

public record Definition(
        List<String> annotations,
        List<String> modifiers,
        Iterable<String> typeParams,
        Type type,
        String name
) implements MethodHeader, Parameter {
    public static Definition from(Type type, String name) {
        return new Definition(Lists.empty(), Lists.empty(), Lists.empty(), type, name);
    }

    public static String joinTypeParams(Iterable<String> typeParams) {
        return typeParams.iter()
                .collect(new Joiner(", "))
                .map((String inner) -> "<" + inner + ">")
                .orElse("");
    }

    @Override
    public String generate() {
        return this.generateWithAfterName("");
    }

    @Override
    public Option<Definition> asDefinition() {
        return new Some<Definition>(this);
    }

    @Override
    public String generateWithAfterName(String afterName) {
        var joinedTypeParams = this.joinTypeParams();
        var joinedModifiers = this.generateModifiers();
        return joinedModifiers + this.type.generateBeforeName() + this.name + joinedTypeParams + afterName + this.generateType();
    }

    private String generateModifiers() {
        return this.modifiers.iter()
                .map((String value) -> value + " ")
                .collect(new Joiner(""))
                .orElse("");
    }

    private String generateType() {
        if (this.type.isVar()) {
            return "";
        }

        return ": " + TypeCompiler.generateType(this.type);
    }

    private String joinTypeParams() {
        return joinTypeParams(this.typeParams);
    }

    @Override
    public boolean hasAnnotation(String annotation) {
        return this.annotations.contains(annotation);
    }

    @Override
    public MethodHeader removeModifier(String modifier) {
        return new Definition(this.annotations, this.modifiers.removeValue(modifier), this.typeParams, this.type, this.name);
    }

    public boolean isNamed(String name) {
        return Strings.equalsTo(this.name, name);
    }
}

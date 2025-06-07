package magma.ast;

import magma.util.*;

import java.util.Objects;
import java.util.function.Function;

public final class Definition implements Parameter, Generating {
    private final Option<String> beforeType;
    private final List<String> typeParams;
    public final Type type;
    public final String name;

    public Definition(
            Option<String> beforeType,
            List<String> typeParams,
            Type type,
            String name
    ) {
        this.beforeType = beforeType;
        this.typeParams = typeParams;
        this.type = type;
        this.name = name;
    }

    public Definition(Type type, String name) {
        this(new None<>(), Lists.empty(), type, name);
    }

    @Override
    public String generate() {
        return generateWithAfterName("");
    }

    public String generateWithAfterName(String afterName) {
        final var joinedTypeParams = typeParams.iter()
                .collect(new Joiner(", "))
                .map(value -> "<" + value + ">")
                .orElse("");

        final var beforeType = this.beforeType.map(inner -> inner + " ").orElse("");
        return beforeType + name + joinedTypeParams + afterName + ": " + type.generate();
    }

    public Definition mapType(Function<Type, Type> mapper) {
        return new Definition(beforeType, typeParams, mapper.apply(type), name);
    }

    public Option<String> beforeType() {
        return beforeType;
    }

    public List<String> typeParams() {
        return typeParams;
    }

    public Type type() {
        return type;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Definition) obj;
        return Objects.equals(this.beforeType, that.beforeType) &&
                Objects.equals(this.typeParams, that.typeParams) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beforeType, typeParams, type, name);
    }

    @Override
    public String toString() {
        return "Definition[" +
                "beforeType=" + beforeType + ", " +
                "typeParams=" + typeParams + ", " +
                "type=" + type + ", " +
                "name=" + name + ']';
    }

}

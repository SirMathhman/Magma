package magma.app.compile.value;

import magma.api.option.None;
import magma.api.option.Option;
import magma.app.TypeCompiler;
import magma.app.compile.define.Definition;
import magma.app.compile.define.Parameter;
import magma.app.compile.type.Type;

public record Placeholder(String value) implements Parameter, Value, Type {
    public static String fromValue(String value) {
        var replaced = value
                .replace("/*", "start")
                .replace("*/", "end");

        return "/*" + replaced + "*/";
    }

    public static String fromNode(Placeholder placeholder) {
        return Placeholder.fromValue(placeholder.value);
    }

    public String generate() {
        return Placeholder.fromValue(this.value);
    }

    @Override
    public boolean isFunctional() {
        return false;
    }

    @Override
    public Option<Definition> asDefinition() {
        return new None<Definition>();
    }

    @Override
    public boolean isVar() {
        return false;
    }

    @Override
    public String generateBeforeName() {
        return "";
    }

    @Override
    public String generateSimple() {
        return TypeCompiler.generateType(this);
    }
}

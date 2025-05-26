package magma.app.compile.value;

import magma.api.option.None;
import magma.api.option.Option;
import magma.app.ValueCompiler;
import magma.app.compile.CompileState;
import magma.app.compile.define.Definition;
import magma.app.compile.define.Parameter;
import magma.app.compile.type.PrimitiveType;
import magma.app.compile.type.Type;

public record Placeholder(String input) implements Parameter, Value, Type {
    public static String generatePlaceholder(String input) {
        var replaced = input
                .replace("/*", "start")
                .replace("*/", "end");

        return "/*" + replaced + "*/";
    }

    @Override
    public String generate() {
        return generatePlaceholder(this.input);
    }

    @Override
    public boolean isFunctional() {
        return false;
    }
    @Override
    public Option<Definition> asDefinition() {
        return new None<Definition>();
    }

    public Type resolve(CompileState state) {
        return PrimitiveType.Unknown;
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
    public Option<String> generateAsEnumValue(String structureName) {
        return new None<String>();
    }

    @Override
    public String generateSimple() {
        return ValueCompiler.getString(this);
    }
}

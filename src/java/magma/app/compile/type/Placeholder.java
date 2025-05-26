package magma.app.compile.type;

import magma.api.option.None;
import magma.api.option.Option;
import magma.app.ValueCompiler;
import magma.app.compile.define.Definition;
import magma.app.compile.define.Parameter;
import magma.app.compile.node.Node;

public record Placeholder(String input) implements Parameter, Node {
    public static String generatePlaceholder(String input) {
        var replaced = input
                .replace("/*", "start")
                .replace("*/", "end");

        return "/*" + replaced + "*/";
    }

    public String generateNode() {
        return Placeholder.generatePlaceholder(this.input);
    }

    @Override
    public String generate() {
        return Placeholder.generatePlaceholder(this.input);
    }

    @Override
    public Option<Definition> asDefinition() {
        return new None<Definition>();
    }

    public String generateBeforeName() {
        return "";
    }

    public String generateSimple() {
        return ValueCompiler.generateValue(this);
    }

    public boolean is(String type) {
        return false;
    }
}

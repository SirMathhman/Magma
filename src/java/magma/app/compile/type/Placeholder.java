package magma.app.compile.type;

import magma.app.compile.define.Parameter;
import magma.app.compile.node.Node;

public record Placeholder(String input) implements Parameter, Node {
    public static String generatePlaceholder(String input) {
        var replaced = input
                .replace("/*", "start")
                .replace("*/", "end");

        return "/*" + replaced + "*/";
    }

    @Override
    public String generate() {
        return Placeholder.generatePlaceholder(this.input);
    }

    public boolean is(String type) {
        return false;
    }
}

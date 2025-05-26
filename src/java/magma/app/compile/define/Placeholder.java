package magma.app.compile.define;

import magma.app.compile.node.Node;

public record Placeholder(String input) implements Node {
    public static String generatePlaceholder(String input) {
        var replaced = input
                .replace("/*", "start")
                .replace("*/", "end");

        return "/*" + replaced + "*/";
    }

    public boolean is(String type) {
        return false;
    }
}

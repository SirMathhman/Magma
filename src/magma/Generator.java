package magma;

import magma.ast.*;
import magma.util.*;

/** Utility methods for generating source code from AST nodes. */
public final class Generator {
    private Generator() {}

    public static <T extends Generating> String generateNodes(List<T> arguments) {
        return arguments.iter()
                .map(Generating::generate)
                .collect(new Joiner(", "))
                .orElse("");
    }

    public static String generatePlaceholder(String input) {
        final var replaced = input
                .replace("/*", "start")
                .replace("*/", "end");
        return "/*" + replaced + "*/";
    }
}

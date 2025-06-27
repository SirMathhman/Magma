package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        try {
            final var input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));
            final var segments = Main.divide(input);

            final var output = new StringBuilder();
            for (final var segment : segments)
                output.append(Main.compileRootSegment(segment));

            final var targetParent = Paths.get(".", "src", "node", "magma");
            if (!Files.exists(targetParent))
                Files.createDirectories(targetParent);

            final var target = targetParent.resolve("Main.ts");
            Files.writeString(target, output);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileRootSegment(final String input) {
        return Main.compileRootSegmentValue(input.strip()) + System.lineSeparator();
    }

    private static String compileRootSegmentValue(final String input) {
        if (input.endsWith("}")) {
            final var withoutEnd = input.substring(0, input.length() - "}".length());
            return Main.generatePlaceholder(withoutEnd) + "}";
        }

        return Main.generatePlaceholder(input);
    }

    private static List<String> divide(final CharSequence input) {
        State current = new MutableState();
        final var length = input.length();
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = Main.fold(current, c);
        }

        return current.advance()
                .unwrap();
    }

    private static State fold(final State state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel())
            return appended.advance();
        if ('{' == c)
            return appended.enter();
        if ('}' == c)
            return appended.exit();
        return appended;
    }

    private static String generatePlaceholder(final String input) {
        return "/*" + input.replace("/*", "stat")
                .replace("*/", "end") + "*/";
    }
}

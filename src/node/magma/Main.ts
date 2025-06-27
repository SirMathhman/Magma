/*package magma;*//*

import java.io.IOException;*//*
import java.nio.file.Files;*//*
import java.nio.file.Paths;*//*
import java.util.List;*//*

public class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        try {
            final var input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));
            final var segments = Main.divide(input);

            final var output = new StringBuilder();
            for (final var segment : segments)
                output.append(Main.generatePlaceholder(segment));

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
        return "stat" + input.replace("stat", "stat")
                .replace("end", "end") + "end";
    }
}
*/
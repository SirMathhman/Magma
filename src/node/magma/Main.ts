/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Paths;*/
/*import java.util.List;*/
/*import java.util.function.Function;*/
class Main {/*
    private Main() {
    }*//*

    public static void main(final String[] args) {
        try {
            final var source = Paths.get(".", "src", "java", "magma", "Main.java");
            final var input = Files.readString(source);

            final var output = Main.compileRoot(input);

            final var targetParent = Paths.get(".", "src", "node", "magma");
            if (!Files.exists(targetParent))
                Files.createDirectories(targetParent);

            final var target = targetParent.resolve("Main.ts");
            Files.writeString(target, output);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }*//*

    private static String compileRoot(final CharSequence input) {
        return Main.compileStatements(input, Main::compileRootSegment);
    }*//*

    private static String compileStatements(final CharSequence input, final Function<String, String> mapper) {
        final var segments = Main.divide(input);
        final var output = new StringBuilder();
        for (final var segment : segments)
            output.append(mapper.apply(segment));

        return output.toString();
    }*//*

    private static String compileRootSegment(final String input) {
        final var strip = input.strip();
        if (strip.startsWith("package "))
            return "";

        if (strip.startsWith("import "))
            return Main.generatePlaceholder(strip) + System.lineSeparator();

        final var contentStart = input.indexOf('{');
        if (0 <= contentStart) {
            final var beforeContent = input.substring(0, contentStart);
            final var withEnd = input.substring(contentStart + "{".length());
            if (withEnd.endsWith("}")) {
                final var content = withEnd.substring(0, withEnd.length() - "}".length());
                return Main.compileClassHeader(beforeContent) + "{" + Main.compileStatements(content,
                        Main::compileClassSegment) + "}";
            }
        }

        return Main.generatePlaceholder(input);
    }*//*

    private static String compileClassSegment(final String input) {
        return Main.generatePlaceholder(input);
    }*//*

    private static String compileClassHeader(final String input) {
        final var classIndex = input.indexOf("class ");
        if (0 <= classIndex) {
            final var oldBeforeKeyword = input.substring(0, classIndex)
                    .strip();

            final var afterKeyword = input.substring(classIndex + "class ".length());
            final var newBeforeKeyword = oldBeforeKeyword.isEmpty() ? "" : Main.generatePlaceholder(oldBeforeKeyword);
            return newBeforeKeyword + "class " + afterKeyword.strip() + " ";
        }

        return Main.generatePlaceholder(input);
    }*//*

    private static List<String> divide(final CharSequence input) {
        final State state = new MutableState();
        final var length = input.length();
        var current = state;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = Main.fold(current, c);
        }

        return current.advance()
                .unwrap();
    }*//*

    private static State fold(final State state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel())
            return appended.advance();
        if ('}*//*' == c && appended.isShallow())
            return appended.exit()
                    .advance();*//*
        if ('{' == c)
            return appended.enter();
        if ('}*//*' == c)
            return appended.exit();*//*
        return appended;*//*
    */}/*

    private static String generatePlaceholder(final String input) */{/*
        final var replaced = input.replace("start", "start")
                .replace("end", "end");*//*

        return "start" + replaced + "end";*//*
    */}/*
}
*/
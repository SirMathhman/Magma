/*package magma;*/
/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Paths;*/
/*import java.util.stream.Collectors;*/
/*import java.util.stream.Stream;*/
/*public class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        try {
            final var input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));

            final var targetParent = Paths.get(".", "src", "node", "magma");
            if (!Files.exists(targetParent))
                Files.createDirectories(targetParent);

            final var target = targetParent.resolve("Main.ts");
            final var output = Main.compile(input);
            Files.writeString(target, output);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(final CharSequence input) {
        return Main.divide(input)
                   .map(String::strip)
                   .map(Main::generatePlaceholder)
                   .map(result -> result + System.lineSeparator())
                   .collect(Collectors.joining());
    }

    private static Stream<String> divide(final CharSequence input) {
        final DivideState state = new MutableDivideState();
        final var length = input.length();
        var current = state;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = Main.fold(current, c);
        }

        return current.advance().stream();
    }

    private static DivideState fold(final DivideState state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel())
            return appended.advance();
        if(c == '{') return appended.enter();
        if(c == '}') return  appended.exit();
        else
            return appended;
    }

    private static String generatePlaceholder(final String input) {
        final var replaced = input.replace("start", "start").replace("end", "end");
        return "start" + replaced + "end";
    }
}*/

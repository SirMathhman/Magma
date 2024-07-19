package magma.app.compile;

import magma.app.ApplicationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Compiler {
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String IMPORT_SEPARATOR = ".";
    public static final String STATEMENT_END = ";";
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";

    public static String compile(String input) throws ApplicationException {
        var lines = split(input).toList();

        var builder = new StringBuilder();
        for (String line : lines) {
            builder.append(compileLine(line.strip()));
        }

        return builder.toString();
    }

    private static Stream<String> split(String input) {
        var state = new State();
        for (int i = 0; i < input.length(); i++) {
            var c = input.charAt(i);
            state = splitAtChar(state, c);
        }

        return state.advance().stream();
    }

    private static State splitAtChar(State state, char c) {
        var appended = state.append(c);
        return c == ';' ? appended.advance() : appended;
    }

    private static String compileLine(String input) throws ApplicationException {
        if (input.isEmpty()) return "";
        return compilePackage(input).or(() -> compileImport(input)).orElseThrow(() -> new ApplicationException("Unknown input: " + input));
    }

    private static Optional<String> compilePackage(String input) {
        return input.startsWith(PACKAGE_KEYWORD_WITH_SPACE) ? Optional.of("") : Optional.empty();
    }

    private static Optional<String> compileImport(String input) {
        if (!input.startsWith(IMPORT_KEYWORD_WITH_SPACE)) return Optional.empty();
        var afterKeyword = input.substring(IMPORT_KEYWORD_WITH_SPACE.length());

        if (!afterKeyword.endsWith(STATEMENT_END)) return Optional.empty();
        return Optional.of(input);
    }

    public static String renderImport(String leftPadding, String parent, String child) {
        return leftPadding + IMPORT_KEYWORD_WITH_SPACE + parent + IMPORT_SEPARATOR + child + STATEMENT_END;
    }

    public static String renderPackage(String name) {
        return PACKAGE_KEYWORD_WITH_SPACE + name + STATEMENT_END;
    }

    private record State(StringBuilder buffer, List<String> segments) {
        private State() {
            this(new StringBuilder(), new ArrayList<>());
        }

        private State advance() {
            if (buffer.isEmpty()) return this;
            var copy = new ArrayList<>(segments);
            copy.add(buffer.toString());
            return new State(new StringBuilder(), copy);
        }

        private State append(char c) {
            return new State(buffer().append(c), this.segments);
        }

        public Stream<String> stream() {
            return segments.stream();
        }
    }
}

package magma;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String STATEMENT_END = ";";
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";

    public static String renderImport(String name) {
        return IMPORT_KEYWORD_WITH_SPACE + name + STATEMENT_END;
    }

    static String compile(String input) throws CompileException {
        var segments = split(input);

        var output = new StringBuilder();
        for (var line : segments) {
            output.append(compileRootMember(line));
        }
        return output.toString();
    }

    private static List<String> split(String input) {
        var state = new State();
        for (int i = 0; i < input.length(); i++) {
            state = splitAtChar(state, input.charAt(i));
        }

        return state.advance().segments;
    }

    private static State splitAtChar(State state, char c) {
        var appended = state.append(c);
        return c == ';' ? appended.advance() : appended;
    }

    private static String compileRootMember(String input) throws CompileException {
        if (input.isEmpty() || input.startsWith(PACKAGE_KEYWORD_WITH_SPACE)) return "";
        return compileImport(input).orElseThrow(() -> new CompileException("Invalid input", input));
    }

    private static Optional<String> compileImport(String input) {
        if (!input.startsWith(IMPORT_KEYWORD_WITH_SPACE)) return Optional.empty();
        var afterKeyword = input.substring(IMPORT_KEYWORD_WITH_SPACE.length());

        if (!afterKeyword.endsWith(STATEMENT_END)) return Optional.empty();
        var name = afterKeyword.substring(0, afterKeyword.length() - STATEMENT_END.length());

        return Optional.of(renderImport(name));
    }

    private static class State {
        private final List<String> segments;
        private final StringBuilder buffer;

        private State() {
            this(new StringBuilder(), new ArrayList<>());
        }

        private State(StringBuilder buffer, List<String> segments) {
            this.buffer = buffer;
            this.segments = segments;
        }

        private State append(char c) {
            return new State(this.buffer.append(c), this.segments);
        }

        private State advance() {
            var copy = new ArrayList<>(segments);
            copy.add(buffer.toString());
            return new State(new StringBuilder(), copy);
        }
    }
}
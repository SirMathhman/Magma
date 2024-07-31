package magma;

import java.util.ArrayList;
import java.util.List;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String STATEMENT_END = ";";
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";

    static String renderPackage(String name) {
        return PACKAGE_KEYWORD_WITH_SPACE + name + STATEMENT_END;
    }

    static String renderImport(String whitespace, String parent, String child) {
        return whitespace + IMPORT_KEYWORD_WITH_SPACE + parent + "." + child + STATEMENT_END;
    }

    private static String compileRootMember(String input) throws ParseException {
        if (input.isEmpty() || input.startsWith(PACKAGE_KEYWORD_WITH_SPACE)) return "";
        if (input.startsWith(IMPORT_KEYWORD_WITH_SPACE)) return input;

        throw new ParseException("Invalid root", input);
    }

    private static List<String> splitRootMembers(String input) {
        var current = new State();
        var length = input.length();
        for (int i = 0; i < length; i++) {
            var c = input.charAt(i);
            current = splitAtChar(current, c);
        }

        return current.advance().segments;
    }

    private static State splitAtChar(State current, char c) {
        var appended = current.append(c);
        return c == ';' ? appended.advance() : appended;
    }

    String compile(String input) throws ParseException {
        var rootMembers = splitRootMembers(input);
        var output = new StringBuilder();
        for (var rootMember : rootMembers) {
            var stripped = rootMember.strip();
            var compiled = compileRootMember(stripped);
            output.append(compiled);
        }

        return output.toString();
    }

    private static class State {
        private final List<String> segments;
        private final StringBuilder buffer;

        private State() {
            this(new ArrayList<>(), new StringBuilder());
        }

        private State(List<String> segments, StringBuilder buffer) {
            this.segments = segments;
            this.buffer = buffer;
        }

        private State advance() {
            var copy = new ArrayList<>(segments);
            copy.add(buffer.toString());
            return new State(copy, new StringBuilder());
        }

        public State append(char c) {
            return new State(segments, buffer.append(c));
        }
    }
}
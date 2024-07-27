package magma.app;

import magma.compile.CompileException;

import java.util.stream.Stream;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final char STATEMENT_END = ';';

    private static String compileRootMember(String input) throws CompileException {
        if (input.isEmpty() || input.startsWith(PACKAGE_KEYWORD_WITH_SPACE)) return "";
        if (input.startsWith(IMPORT_KEYWORD_WITH_SPACE)) return input;

        throw new CompileException("Invalid root member", input);
    }

    private static Stream<String> split(String input) {
        var length = input.length();
        var state = new State();
        for (int i = 0; i < length; i++) {
            var c = input.charAt(i);
            state = splitAtChar(state, c);
        }

        return state.advance().stream();
    }

    private static State splitAtChar(State state, char c) {
        var appended = state.append(c);
        if (c == STATEMENT_END) {
            return appended.advance();
        } else {
            return appended;
        }
    }

    String compile(String input) throws CompileException {
        var rootMembers = split(input).toList();

        var output = new StringBuilder();
        for (String rootMember : rootMembers) {
            output.append(compileRootMember(rootMember));
        }

        return output.toString();
    }
}
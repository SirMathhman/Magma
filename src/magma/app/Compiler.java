package magma.app;

import magma.compile.CompileException;

import java.util.ArrayList;
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

    String compile(String input) throws CompileException {
        var rootMembers = split(input).toList();

        var output = new StringBuilder();
        for (String rootMember : rootMembers) {
            output.append(compileRootMember(rootMember));
        }

        return output.toString();
    }

    private static Stream<String> split(String input) {
        var rootMembers = new ArrayList<String>();
        var buffer = new StringBuilder();
        var length = input.length();
        for (int i = 0; i < length; i++) {
            var c = input.charAt(i);
            buffer.append(c);
            if (c == STATEMENT_END) {
                rootMembers.add(buffer.toString());
                buffer = new StringBuilder();
            }
        }
        rootMembers.add(buffer.toString());
        return rootMembers.stream();
    }
}
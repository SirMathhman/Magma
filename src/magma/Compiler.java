package magma;

import java.util.ArrayList;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String STATEMENT_END = ";";
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";

    static String renderPackage(String name) {
        return PACKAGE_KEYWORD_WITH_SPACE + name + STATEMENT_END;
    }

    static String renderImport(String parent, String child) {
        return IMPORT_KEYWORD_WITH_SPACE + parent + "." + child + STATEMENT_END;
    }

    private static String compileRootMember(String input) throws ParseException {
        if (input.isEmpty() || input.startsWith(PACKAGE_KEYWORD_WITH_SPACE)) return "";
        if (input.startsWith(IMPORT_KEYWORD_WITH_SPACE)) return input;

        throw new ParseException("Invalid root", input);
    }

    private static ArrayList<String> splitRootMembers(String input) {
        var segments = new ArrayList<String>();
        var buffer = new StringBuilder();
        var length = input.length();
        for (int i = 0; i < length; i++) {
            var c = input.charAt(i);
            buffer.append(c);
            if (c == ';') {
                segments.add(buffer.toString());
                buffer = new StringBuilder();
            }
        }
        return segments;
    }

    String compile(String input) throws ParseException {
        var rootMembers = splitRootMembers(input);
        var output = new StringBuilder();
        for (var rootMember : rootMembers) {
            output.append(compileRootMember(rootMember));
        }

        return output.toString();
    }
}
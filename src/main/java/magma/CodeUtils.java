package magma;

/**
 * Utility class for common code processing functions.
 */
public class CodeUtils {
    /**
     * Find the matching closing brace for the opening brace at the given position.
     *
     * @param code    The code string
     * @param openIdx The position of the opening brace
     * @return The position of the matching closing brace
     * @throws CompileException If there is no matching closing brace
     */
    public static int findMatchingBrace(String code, int openIdx) throws CompileException {
        int depth = 0;
        for (int j = openIdx; j < code.length(); j++) {
            char cj = code.charAt(j);
            if (cj == '{') depth++;
            else if (cj == '}') {
                depth--;
                if (depth == 0) return j;
            }
        }
        throw new CompileException("Unmatched '{'", code);
    }
}
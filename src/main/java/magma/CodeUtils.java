package magma;

import magma.node.FunctionPositions;

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
    
    /**
     * Finds the matching closing parenthesis for the opening parenthesis at the given position.
     *
     * @param code    The code string
     * @param openPos The position of the opening parenthesis
     * @return The position of the matching closing parenthesis, or -1 if not found
     */
    public static int findMatchingParenthesis(String code, int openPos) {
        if (openPos >= code.length() || code.charAt(openPos) != '(') {
            return -1;
        }

        int count = 1;
        for (int i = openPos + 1; i < code.length(); i++) {
            char c = code.charAt(i);
            if (c == '(') {
                count++;
            } else if (c == ')') {
                count--;
                if (count == 0) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Validates a potential function declaration and finds its key positions.
     *
     * @param code The code to analyze
     * @param pos The position of "fn " in the code
     * @return A FunctionPositions object with all positions, or null if invalid
     */
    public static FunctionPositions validateAndFindFunctionPositions(String code, int pos) {
        // Skip if this is not a function declaration (e.g., it's part of a string)
        if (pos > 0 && !Character.isWhitespace(code.charAt(pos - 1)) && 
            code.charAt(pos - 1) != '{' && code.charAt(pos - 1) != ';') {
            System.out.println("[DEBUG_LOG] Skipping as this doesn't appear to be a function declaration");
            return null;
        }
        
        FunctionPositions positions = new FunctionPositions();
        
        // Find the function name
        positions.nameStart = pos + 3; // Skip "fn "
        positions.nameEnd = code.indexOf("(", positions.nameStart);
        if (positions.nameEnd != -1) {
            String functionName = code.substring(positions.nameStart, positions.nameEnd).trim();
            System.out.println("[DEBUG_LOG] Detected inner function name: " + functionName);
        }
        
        // Find the function declaration end (opening brace)
        positions.arrowPos = code.indexOf("=>", pos);
        if (positions.arrowPos == -1) {
            System.out.println("[DEBUG_LOG] No arrow found, skipping");
            return null;
        }
        
        positions.openBracePos = code.indexOf("{", positions.arrowPos);
        if (positions.openBracePos == -1) {
            System.out.println("[DEBUG_LOG] No opening brace found, skipping");
            return null;
        }
        
        // Find the matching closing brace
        try {
            positions.closeBracePos = findMatchingBrace(code, positions.openBracePos);
        } catch (CompileException e) {
            System.out.println("[DEBUG_LOG] No matching closing brace found, skipping: " + e.getMessage());
            return null;
        }
        
        return positions;
    }
}
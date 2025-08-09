package magma.validation;

import magma.params.OperatorCheckParams;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for checking operators in expressions.
 * This class extracts operator checking functionality from the Compiler class
 * to reduce method count and cyclomatic complexity.
 */
public class OperatorChecker {
    // Map of two-character operators to their second characters
    private final Map<String, Character> twoCharOperators;
    
    /**
     * Creates a new OperatorChecker with predefined two-character operators.
     */
    public OperatorChecker() {
        twoCharOperators = new HashMap<>();
        twoCharOperators.put("||", '|');
        twoCharOperators.put("&&", '&');
        twoCharOperators.put("==", '=');
        twoCharOperators.put("!=", '=');
        twoCharOperators.put("<=", '=');
        twoCharOperators.put(">=", '=');
    }
    
    /**
     * Checks if the second character of a two-character operator matches.
     * Uses a map-based approach to reduce cyclomatic complexity.
     *
     * @param params the parameters for checking an operator
     * @return the index if found, or -1 if not a match
     */
    public int checkSecondCharOfOperator(OperatorCheckParams params) {
        String expression = params.expression();
        int index = params.index();
        String operator = params.operator();
        
        // Check if we have enough characters left in the expression
        if (index + 1 >= expression.length()) return -1;
        
        // Get the expected second character for this operator
        Character expectedSecondChar = twoCharOperators.get(operator);
        if (expectedSecondChar == null) return -1;  // Not a two-character operator
        
        // Check if the second character matches the expected one
        char secondChar = expression.charAt(index + 1);
        if (secondChar == expectedSecondChar) return index;
        
        return -1;
    }
}

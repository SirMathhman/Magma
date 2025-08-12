package magma;

class SyntaxValidator {
    
    static void validateSyntax(String stmt) throws CompileException {
        validateBasicSyntax(stmt);
        validateOperators(stmt);
        validateParentheses(stmt);
        validateStringLiterals(stmt);
        validateReturnStatement(stmt);
    }

    private static void validateBasicSyntax(String stmt) throws CompileException {
        if (stmt.trim().equals("{}")) {
            throw new CompileException("Empty braces are not allowed");
        }
        
        if (stmt.trim().equals("}") || stmt.trim().equals(")") || stmt.trim().equals("]")) {
            throw new CompileException("Stray closing bracket/brace/paren");
        }
    }

    private static void validateOperators(String stmt) throws CompileException {
        if (stmt.matches(".*\\+\\s*;?$") || stmt.matches(".*-\\s*;?$") || stmt.matches(".*\\*\\s*;?$") || stmt.matches(".*\\/\\s*;?$")) {
            throw new CompileException("Dangling operator in expression");
        }
        
        if (stmt.matches("^\\s*[+\\-*/].*")) {
            throw new CompileException("Missing operand before operator");
        }
        
        if (stmt.matches(".*[+\\-*/]\\s*[+\\-*/].*")) {
            throw new CompileException("Double operator in expression");
        }
    }

    private static void validateParentheses(String stmt) throws CompileException {
        int parenCount = 0;
        for (char c : stmt.toCharArray()) {
            if (c == '(') parenCount++;
            else if (c == ')') parenCount--;
            if (parenCount < 0) throw new CompileException("Unbalanced parentheses");
        }
        if (parenCount != 0) throw new CompileException("Unbalanced parentheses");
    }

    private static void validateStringLiterals(String stmt) throws CompileException {
        boolean inString = false;
        for (int i = 0; i < stmt.length(); i++) {
            char c = stmt.charAt(i);
            if (c == '"' && (i == 0 || stmt.charAt(i-1) != '\\')) {
                inString = !inString;
            }
        }
        if (inString) throw new CompileException("Unterminated string literal");
    }

    private static void validateReturnStatement(String stmt) throws CompileException {
        if (stmt.matches("^\\s*return\\s*;?\\s*$")) {
            throw new CompileException("Return statement missing value");
        }
    }
}
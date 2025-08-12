package magma;

class ConstructValidator {
    
    static void validateConstructSyntax(String construct) throws CompileException {
        if (construct.startsWith("fn ")) {
            validateFunctionSyntax(construct);
        } else if (construct.startsWith("struct ")) {
            validateStructSyntax(construct);
        } else if (construct.startsWith("class fn ")) {
            validateClassSyntax(construct);
        }
    }

    private static void validateFunctionSyntax(String construct) throws CompileException {
        if (construct.matches("^fn\\s*\\(.*")) {
            throw new CompileException("Function missing name");
        }
        if (!construct.contains("(") || !construct.contains(")")) {
            throw new CompileException("Function missing parentheses");
        }
        if (!construct.contains("=>")) {
            throw new CompileException("Function missing arrow");
        }
        if (!construct.contains("{") || !construct.contains("}")) {
            throw new CompileException("Function missing body");
        }
        if (construct.matches(".*\\([^)]*\\w+\\s+\\w+.*")) {
            throw new CompileException("Invalid function parameter syntax");
        }
    }

    private static void validateStructSyntax(String construct) throws CompileException {
        if (construct.matches("^struct\\s*\\{.*")) {
            throw new CompileException("Struct missing name");
        }
        if (!construct.contains("{") || !construct.contains("}")) {
            throw new CompileException("Struct missing braces");
        }
    }

    private static void validateClassSyntax(String construct) throws CompileException {
        if (!construct.contains("(") || !construct.contains(")")) {
            throw new CompileException("Class missing parentheses");
        }
        if (!construct.contains("=>")) {
            throw new CompileException("Class missing arrow");
        }
        if (!construct.contains("{") || !construct.contains("}")) {
            throw new CompileException("Class missing body");
        }
    }
}
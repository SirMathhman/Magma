package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pretty printer for C code output from the Magma compiler.
 * Formats compact C code into readable, properly indented code.
 */
class CPrettyPrinter {
    
    public static String prettyPrint(String compactC) {
        if (compactC == null || compactC.trim().isEmpty()) {
            return compactC;
        }
        
        StringBuilder result = new StringBuilder();
        String[] statements = splitIntoStatements(compactC);
        
        for (int i = 0; i < statements.length; i++) {
            String statement = statements[i].trim();
            if (!statement.isEmpty()) {
                if (i > 0) {
                    result.append("\n\n");
                }
                result.append(formatStatement(statement));
            }
        }
        
        return result.toString();
    }
    
    private static String[] splitIntoStatements(String compactC) {
        // Split on major construct boundaries
        // Look for patterns like "} type name(" or ";}type" 
        String withMarkers = compactC
            .replaceAll("(#include\\s+[<\"][^>\"]+[>\"])\\s+", "$1\n\n")  // Split #include from following code
            .replaceAll("(})\\s*([a-zA-Z_][\\w\\s]*\\s+[a-zA-Z_]\\w*\\s*\\()", "$1\n\n$2")
            .replaceAll("(;)\\s*(struct\\s+\\w+\\s*\\{)", "$1\n\n$2")  // Split struct definitions
            .replaceAll("(;)\\s*(struct\\s+\\w+\\s+\\w+)", "$1\n\n$2")
            .replaceAll("(;)\\s*([a-zA-Z_][\\w\\s]*\\s+[a-zA-Z_]\\w*\\s*\\()", "$1\n\n$2")
            .replaceAll("(})\\s*(struct\\s+)", "$1\n\n$2")
            .replaceAll("(;)\\s*([a-zA-Z_][\\w\\s\\*]*\\s+[a-zA-Z_]\\w*\\s*=)", "$1\n\n$2");
        
        return withMarkers.split("\n\n");
    }
    
    private static String formatStatement(String statement) {
        // Check if this is a struct definition
        if (statement.startsWith("struct ") && statement.contains("{") && statement.endsWith("};")) {
            return formatStruct(statement);
        }
        
        // Check if this is a function definition
        if (isFunctionDefinition(statement)) {
            return formatFunction(statement);
        }
        
        // For other statements, just return as-is with proper spacing
        return statement;
    }
    
    private static boolean isFunctionDefinition(String statement) {
        // Look for patterns like "type name(...) { ... }"
        Pattern functionPattern = Pattern.compile("^[\\w\\s\\*]+\\s+\\w+\\s*\\([^)]*\\)\\s*\\{.*}$", Pattern.DOTALL);
        return functionPattern.matcher(statement).matches();
    }
    
    private static String formatStruct(String struct) {
        // Pattern: struct Name {fields};
        Pattern structPattern = Pattern.compile("^struct\\s+(\\w+)\\s*\\{([^}]*)}\\s*;?$");
        Matcher matcher = structPattern.matcher(struct);
        
        if (!matcher.matches()) {
            return struct; // Return as-is if we can't parse it
        }
        
        String structName = matcher.group(1);
        String fields = matcher.group(2).trim();
        
        StringBuilder result = new StringBuilder();
        result.append("struct ").append(structName).append(" {");
        
        if (fields.isEmpty()) {
            result.append("\n");
        } else {
            result.append("\n");
            // Split fields on semicolons and format each
            String[] fieldArray = fields.split(";");
            for (String field : fieldArray) {
                field = field.trim();
                if (!field.isEmpty()) {
                    result.append("    ").append(field).append(";\n");
                }
            }
        }
        
        result.append("};");
        return result.toString();
    }
    
    private static String formatFunction(String function) {
        // Extract function signature and body
        Pattern functionPattern = Pattern.compile("^([\\w\\s\\*]+\\s+\\w+\\s*\\([^)]*\\))\\s*\\{(.*)}$", Pattern.DOTALL);
        Matcher matcher = functionPattern.matcher(function);
        
        if (!matcher.matches()) {
            return function; // Return as-is if we can't parse it
        }
        
        String signature = matcher.group(1).trim();
        String body = matcher.group(2).trim();
        
        StringBuilder result = new StringBuilder();
        result.append(signature).append(" {\n");
        
        if (!body.isEmpty()) {
            // Split body into statements and indent each
            String[] statements = body.split(";");
            for (String stmt : statements) {
                stmt = stmt.trim();
                if (!stmt.isEmpty()) {
                    result.append("    ").append(stmt).append(";\n");
                }
            }
        }
        
        result.append("}");
        return result.toString();
    }
}
package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles compilation of variable declarations without explicit type annotations in Magma.
 */
public class ImplicitTypeCompiler {
    private static final Pattern LET_PATTERN = Pattern.compile("let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([^;]+);");
    private static final Pattern CHAR_LITERAL_PATTERN = Pattern.compile("'(.)'");
    private static final Pattern TYPE_SUFFIX_PATTERN = Pattern.compile("(\\d+)([IU][0-9]+)");

    /**
     * Tries to compile a declaration without explicit type annotation: "let x = value;"
     * Also handles type suffixes like "let x = 100U64;" and character literals like 'a'
     * 
     * @param input The input string to compile
     * @return The compiled C code, or an empty string if the input doesn't match an implicit type declaration
     * @throws CompileException If the compilation fails
     */
    public static String tryCompile(String input) throws CompileException {
        Matcher matcher = LET_PATTERN.matcher(input);
        
        if (!matcher.find()) {
            return "";
        }
        
        String variableName = matcher.group(1);
        String value = matcher.group(2);
        
        // Check if the value is a character literal (like 'a')
        Matcher charLiteralMatcher = CHAR_LITERAL_PATTERN.matcher(value);
        
        if (charLiteralMatcher.matches()) {
            // Character literals are automatically assigned U8 type
            char character = charLiteralMatcher.group(1).charAt(0);
            return "uint8_t " + variableName + " = " + (int) character + ";";
        }
        
        // Check if the value has a type suffix (like 100U64)
        Matcher typeSuffixMatcher = TYPE_SUFFIX_PATTERN.matcher(value);
        
        if (typeSuffixMatcher.matches()) {
            String baseValue = typeSuffixMatcher.group(1);
            String typeSuffix = typeSuffixMatcher.group(2);
            
            String cType = TypeMapper.getCType(typeSuffix);
            if (cType == null) throw new CompileException();
            
            return cType + " " + variableName + " = " + baseValue + ";";
        }
        
        // Check for boolean literals
        if (value.equals("true") || value.equals("false")) {
            return "bool " + variableName + " = " + value + ";";
        }
        
        // Default to int32_t if no type suffix and not a boolean literal or character literal
        return "int32_t " + variableName + " = " + value + ";";
    }
}
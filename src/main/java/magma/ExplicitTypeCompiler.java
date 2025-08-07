package magma;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles compilation of variable declarations with explicit type annotations in Magma.
 */
public class ExplicitTypeCompiler {
    private static final Pattern LET_PATTERN_WITH_TYPE =
            Pattern.compile("let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*([IU][0-9]+|Bool)\\s*=\\s*([^;]+);");
    private static final Pattern CHAR_LITERAL_PATTERN = Pattern.compile("'(.)'");
    private static final Pattern TYPE_SUFFIX_PATTERN = Pattern.compile("(\\d+)([IU][0-9]+)");

    /**
     * Tries to compile a declaration with explicit type annotation: "let x : TYPE = value;"
     * 
     * @param input The input string to compile
     * @return An Optional containing the compiled C code, or empty if the input doesn't match an explicit type declaration
     * @throws CompileException If the compilation fails
     */
    public static Optional<String> tryCompile(String input) throws CompileException {
        Matcher matcherWithType = LET_PATTERN_WITH_TYPE.matcher(input);
        
        if (!matcherWithType.find()) {
            return Optional.empty();
        }
        
        String variableName = matcherWithType.group(1);
        String typeAnnotation = matcherWithType.group(2);
        String value = matcherWithType.group(3);
        
        String cType = TypeMapper.getCType(typeAnnotation);
        if (cType == null) throw new CompileException();
        
        // Special handling for Bool type
        if (typeAnnotation.equals("Bool") && (!value.equals("true") && !value.equals("false")))
            throw new CompileException();
        
        // Process the value based on its format
        value = processValue(value, typeAnnotation);
        
        return Optional.of(cType + " " + variableName + " = " + value + ";");
    }
    
    /**
     * Processes the value part of a variable declaration with explicit type annotation.
     */
    private static String processValue(String value, String typeAnnotation) throws CompileException {
        // Check if the value is a character literal (like 'a')
        Matcher charLiteralMatcher = CHAR_LITERAL_PATTERN.matcher(value);
        
        if (charLiteralMatcher.matches()) {
            // Character literals are only allowed with U8 type
            if (!typeAnnotation.equals("U8")) throw new CompileException();
            
            // Get the character and convert it to its ASCII/Unicode value
            char character = charLiteralMatcher.group(1).charAt(0);
            return String.valueOf((int) character);
        }
        
        // Check if the value has a type suffix (like 100U64)
        Matcher typeSuffixMatcher = TYPE_SUFFIX_PATTERN.matcher(value);
        
        if (typeSuffixMatcher.matches()) {
            String baseValue = typeSuffixMatcher.group(1);
            String typeSuffix = typeSuffixMatcher.group(2);
            
            // Check if the type suffix is compatible with the explicit type annotation
            if (!typeAnnotation.equals(typeSuffix)) throw new CompileException();
            
            // Use the base value without the type suffix
            return baseValue;
        }
        
        // Return the original value if no special processing is needed
        return value;
    }
}
import magma.Compiler;
import magma.CompileException;

public class test_if_statement {
    public static void main(String[] args) {
        try {
            String input = "if(true){}";
            System.out.println("Input: " + input);
            
            // Debug the pattern matching
            java.util.regex.Pattern ifPattern = java.util.regex.Pattern.compile("if\\s*\\(([^)]*)\\)\\s*\\{([^}]*)\\}");
            java.util.regex.Matcher ifMatcher = ifPattern.matcher(input);
            System.out.println("Pattern matches: " + ifMatcher.matches());
            if (ifMatcher.matches()) {
                System.out.println("Condition: " + ifMatcher.group(1));
                System.out.println("Body: " + ifMatcher.group(2));
            }
            
            String output = Compiler.compile(input);
            System.out.println("Output: " + output);
            System.out.println("Compilation successful!");
        } catch (CompileException e) {
            System.out.println("Compilation failed with CompileException");
        } catch (Exception e) {
            System.out.println("Compilation failed with exception: " + e.getClass().getName());
            e.printStackTrace();
        }
    }
}
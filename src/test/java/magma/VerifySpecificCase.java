package magma;

public class VerifySpecificCase {
    public static void main(String[] args) {
        String input = "let x : U16 = 200I32;";
        
        try {
            String output = Compiler.compile(input);
            System.out.println("Compilation succeeded unexpectedly!");
            System.out.println("Output: " + output);
        } catch (CompileException e) {
            System.out.println("Compilation failed as expected!");
        }
    }
}
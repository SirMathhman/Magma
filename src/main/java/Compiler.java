public class Compiler {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }

    public static String compile(String input) throws CompileException {
        if (input == null) {
            throw new CompileException("input cannot be null");
        }
        // simple validation: treat the literal "test" as invalid input
        if ("test".equals(input)) {
            throw new CompileException("invalid input: " + input);
        }
        return input;
    }

    
}

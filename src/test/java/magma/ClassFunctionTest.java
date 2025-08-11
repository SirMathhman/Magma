package magma;

import org.junit.jupiter.api.Test;

class ClassFunctionTest extends CompilerTestBase {
    @Test
    void classWithMethod() {
        String input = "class fn Calculator() => { fn add() => { } }";
        // Test what the current output is
        try {
            String result = Compiler.compile(input);
            System.out.println("Result: '" + result + "'");
        } catch (CompileException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Test
    void classWithMethodAndMain() {
        String input = "class fn Calculator() => {\n fn add() => {\n }\n}\n\nfn main() => {\n    return 0;\n}";
        // Test what happens when we add a main function after the class
        try {
            String result = Compiler.compile(input);
            System.out.println("Result with main: '" + result + "'");
        } catch (CompileException e) {
            System.out.println("Error with main: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
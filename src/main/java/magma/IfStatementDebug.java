package magma;

import magma.core.Compiler;

/**
 * Simple class to debug if statement implementation.
 */
public class IfStatementDebug {
    public static void main(String[] args) {
        Compiler compiler = new Compiler();
        
        // Test basic if statement with true condition
        String ifWithTrue = "if (true) { let x = 10; }";
        System.out.println("Input: " + ifWithTrue);
        String result1 = compiler.compile(ifWithTrue);
        System.out.println("Result: " + result1);
        
        // Test basic if statement with false condition
        String ifWithFalse = "if (false) { let x = 10; }";
        System.out.println("\nInput: " + ifWithFalse);
        String result2 = compiler.compile(ifWithFalse);
        System.out.println("Result: " + result2);
        
        // Test if statement with boolean variable
        String ifWithVariable = "let condition : Bool = true; if (condition) { let x = 10; }";
        System.out.println("\nInput: " + ifWithVariable);
        String result3 = compiler.compile(ifWithVariable);
        System.out.println("Result: " + result3);
    }
}
package com.magma.compiler;

/**
 * Main class for manual testing of the Java to TypeScript compiler.
 * This is a temporary solution until we can set up proper Maven testing.
 */
public class Main {
    
    public static void main(String[] args) {
        // Create an instance of our compiler
        JavaToTypeScriptCompiler compiler = new JavaToTypeScriptCompiler();
        
        // Test case 1: Empty class
        String javaCode1 = "package com.example;\n\n" +
                          "public class EmptyClass {\n" +
                          "}\n";
        
        try {
            String typeScriptCode1 = compiler.compile(javaCode1);
            System.out.println("Test case 1: Empty class");
            System.out.println("Java code:");
            System.out.println(javaCode1);
            System.out.println("TypeScript code:");
            System.out.println(typeScriptCode1);
            
            // Manual assertions
            boolean test1 = typeScriptCode1 != null && 
                           typeScriptCode1.contains("export class EmptyClass") &&
                           !typeScriptCode1.contains("public");
            
            System.out.println("Test passed: " + test1);
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Test case 2: Class with fields
        String javaCode2 = "package com.example;\n\n" +
                          "public class Person {\n" +
                          "    private String name;\n" +
                          "    private int age;\n" +
                          "    public boolean active;\n" +
                          "}\n";
        
        try {
            String typeScriptCode2 = compiler.compile(javaCode2);
            System.out.println("Test case 2: Class with fields");
            System.out.println("Java code:");
            System.out.println(javaCode2);
            System.out.println("TypeScript code:");
            System.out.println(typeScriptCode2);
            
            // Manual assertions
            boolean test2 = typeScriptCode2 != null && 
                           typeScriptCode2.contains("export class Person") &&
                           typeScriptCode2.contains("private name: string;") &&
                           typeScriptCode2.contains("private age: number;") &&
                           typeScriptCode2.contains("public active: boolean;");
            
            System.out.println("Test passed: " + test2);
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
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
        
        // Test case 3: Class with methods
        String javaCode3 = "package com.example;\n\n" +
                          "public class Calculator {\n" +
                          "    public int add(int a, int b) {\n" +
                          "        return a + b;\n" +
                          "    }\n\n" +
                          "    private double multiply(double x, double y) {\n" +
                          "        return x * y;\n" +
                          "    }\n\n" +
                          "    public void printMessage(String message) {\n" +
                          "        System.out.println(message);\n" +
                          "    }\n" +
                          "}\n";
        
        try {
            String typeScriptCode3 = compiler.compile(javaCode3);
            System.out.println("Test case 3: Class with methods");
            System.out.println("Java code:");
            System.out.println(javaCode3);
            System.out.println("TypeScript code:");
            System.out.println(typeScriptCode3);
            
            // Manual assertions
            boolean test3 = typeScriptCode3 != null && 
                           typeScriptCode3.contains("export class Calculator") &&
                           typeScriptCode3.contains("public add(a: number, b: number): number") &&
                           typeScriptCode3.contains("private multiply(x: number, y: number): number") &&
                           typeScriptCode3.contains("public printMessage(message: string): void");
            
            System.out.println("Test passed: " + test3);
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Test case 4: Complex features (constructor, inheritance, interface)
        String javaCode4 = "package com.example;\n\n" +
                          "public interface Printable {\n" +
                          "    void print();\n" +
                          "}\n\n" +
                          "public class Animal {\n" +
                          "    protected String species;\n\n" +
                          "    public Animal(String species) {\n" +
                          "        this.species = species;\n" +
                          "    }\n\n" +
                          "    public String getSpecies() {\n" +
                          "        return species;\n" +
                          "    }\n" +
                          "}\n\n" +
                          "public class Dog extends Animal implements Printable {\n" +
                          "    private String name;\n\n" +
                          "    public Dog(String name) {\n" +
                          "        super(\"Canine\");\n" +
                          "        this.name = name;\n" +
                          "    }\n\n" +
                          "    @Override\n" +
                          "    public void print() {\n" +
                          "        System.out.println(\"Dog: \" + name + \", Species: \" + species);\n" +
                          "    }\n" +
                          "}\n";
        
        try {
            // Use the V2 compiler for complex features
            JavaToTypeScriptCompilerV2 compilerV2 = new JavaToTypeScriptCompilerV2();
            String typeScriptCode4 = compilerV2.compile(javaCode4);
            System.out.println("Test case 4: Complex features");
            System.out.println("Java code:");
            System.out.println(javaCode4);
            System.out.println("TypeScript code:");
            System.out.println(typeScriptCode4);
            
            // Manual assertions
            boolean test4 = typeScriptCode4 != null && 
                           typeScriptCode4.contains("export interface Printable") &&
                           typeScriptCode4.contains("print(): void;") &&
                           typeScriptCode4.contains("export class Animal") &&
                           typeScriptCode4.contains("constructor(species: string)") &&
                           typeScriptCode4.contains("export class Dog extends Animal implements Printable") &&
                           typeScriptCode4.contains("constructor(name: string)") &&
                           typeScriptCode4.contains("super(\"Canine\");");
            
            System.out.println("Test passed: " + test4);
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
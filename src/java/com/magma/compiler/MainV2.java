package com.magma.compiler;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Enhanced Main class for testing the Java to TypeScript compiler,
 * including the self-hosting capability.
 */
public class MainV2 {

	public static void main(final String[] args) {
		// Run basic tests
		MainV2.runBasicTests();

		// Run self-hosting test
		MainV2.runSelfHostingTest();
	}

	/**
	 * Runs basic tests for the compiler.
	 */
	private static void runBasicTests() {
		// Create an instance of our compiler
		final JavaToTypeScriptCompiler compiler = new JavaToTypeScriptCompiler();

		// Test case 1: Empty class
		final String javaCode1 = "package com.example;\n\n" + "public class EmptyClass {\n" + "}\n";

		try {
			final String typeScriptCode1 = compiler.compile(javaCode1);
			System.out.println("Test case 1: Empty class");
			System.out.println("Java code:");
			System.out.println(javaCode1);
			System.out.println("TypeScript code:");
			System.out.println(typeScriptCode1);

			// Manual assertions
			final boolean test1 = null != typeScriptCode1 && typeScriptCode1.contains("export class EmptyClass") &&
														!typeScriptCode1.contains("public");

			System.out.println("Test passed: " + test1);
			System.out.println();

		} catch (final Exception e) {
			System.err.println("Test failed with exception: " + e.getMessage());
			e.printStackTrace();
		}

		// Test case 2: Class with fields
		final String javaCode2 = "package com.example;\n\n" + "public class Person {\n" + "    private String name;\n" +
														 "    private int age;\n" + "    public boolean active;\n" + "}\n";

		try {
			final String typeScriptCode2 = compiler.compile(javaCode2);
			System.out.println("Test case 2: Class with fields");
			System.out.println("Java code:");
			System.out.println(javaCode2);
			System.out.println("TypeScript code:");
			System.out.println(typeScriptCode2);

			// Manual assertions
			final boolean test2 = null != typeScriptCode2 && typeScriptCode2.contains("export class Person") &&
														typeScriptCode2.contains("private name: string;") &&
														typeScriptCode2.contains("private age: number;") &&
														typeScriptCode2.contains("public active: boolean;");

			System.out.println("Test passed: " + test2);
			System.out.println();

		} catch (final Exception e) {
			System.err.println("Test failed with exception: " + e.getMessage());
			e.printStackTrace();
		}

		// Test case 3: Class with methods
		final String javaCode3 =
				"package com.example;\n\n" + "public class Calculator {\n" + "    public int add(int a, int b) {\n" +
				"        return a + b;\n" + "    }\n\n" + "    private double multiply(double x, double y) {\n" +
				"        return x * y;\n" + "    }\n\n" + "    public void printMessage(String message) {\n" +
				"        System.out.println(message);\n" + "    }\n" + "}\n";

		try {
			final String typeScriptCode3 = compiler.compile(javaCode3);
			System.out.println("Test case 3: Class with methods");
			System.out.println("Java code:");
			System.out.println(javaCode3);
			System.out.println("TypeScript code:");
			System.out.println(typeScriptCode3);

			// Manual assertions
			final boolean test3 = null != typeScriptCode3 && typeScriptCode3.contains("export class Calculator") &&
														typeScriptCode3.contains("public add(a: number, b: number): number") &&
														typeScriptCode3.contains("private multiply(x: number, y: number): number") &&
														typeScriptCode3.contains("public printMessage(message: string): void");

			System.out.println("Test passed: " + test3);
			System.out.println();

		} catch (final Exception e) {
			System.err.println("Test failed with exception: " + e.getMessage());
			e.printStackTrace();
		}

		// Test case 4: Complex features (constructor, inheritance, interface)
		final String javaCode4 = "package com.example;\n\n" + "public interface Printable {\n" + "    void print();\n" + "}\n\n" +
														 "public class Animal {\n" + "    protected String species;\n\n" +
														 "    public Animal(String species) {\n" + "        this.species = species;\n" + "    }\n\n" +
														 "    public String getSpecies() {\n" + "        return species;\n" + "    }\n" + "}\n\n" +
														 "public class Dog extends Animal implements Printable {\n" + "    private String name;\n\n" +
														 "    public Dog(String name) {\n" + "        super(\"Canine\");\n" +
														 "        this.name = name;\n" + "    }\n\n" + "    @Override\n" + "    public void print() {\n" +
														 "        System.out.println(\"Dog: \" + name + \", Species: \" + species);\n" + "    }\n" +
														 "}\n";

		try {
			// Use the V2 compiler for complex features
			final JavaToTypeScriptCompilerV2 compilerV2 = new JavaToTypeScriptCompilerV2();
			final String typeScriptCode4 = compilerV2.compile(javaCode4);
			System.out.println("Test case 4: Complex features");
			System.out.println("Java code:");
			System.out.println(javaCode4);
			System.out.println("TypeScript code:");
			System.out.println(typeScriptCode4);

			// Manual assertions
			final boolean test4 = null != typeScriptCode4 && typeScriptCode4.contains("export interface Printable") &&
														typeScriptCode4.contains("print(): void;") && typeScriptCode4.contains("export class Animal") &&
														typeScriptCode4.contains("constructor(species: string)") &&
														typeScriptCode4.contains("export class Dog extends Animal implements Printable") &&
														typeScriptCode4.contains("constructor(name: string)") &&
														typeScriptCode4.contains("super(\"Canine\");");

			System.out.println("Test passed: " + test4);
			System.out.println();

		} catch (final Exception e) {
			System.err.println("Test failed with exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Runs a test for the self-hosting capability of the compiler.
	 */
	private static void runSelfHostingTest() {
		System.out.println("Test case 5: Self-hosting capability");
		System.out.println("--------------------------------");

		try {
			// Create output directory for TypeScript files
			final String outputDir = "out/typescript";
			final Path outputPath = Paths.get(outputDir);
			if (!Files.exists(outputPath)) Files.createDirectories(outputPath);

			// Create an instance of the self-hosted compiler
			final SelfHostedCompiler selfHostedCompiler = new SelfHostedCompiler();

			// Compile a specific file as a test
			final String inputFile = "src/java/com/magma/compiler/JavaToTypeScriptCompilerV2.java";
			final String outputFile = outputDir + "/JavaToTypeScriptCompilerV2.ts";

			final boolean success = selfHostedCompiler.compileFile(inputFile, outputFile);

			if (success) {
				System.out.println("Successfully compiled: " + inputFile + " -> " + outputFile);

				// Read the generated TypeScript file to verify it
				final String typeScriptCode = new String(Files.readAllBytes(Paths.get(outputFile)), StandardCharsets.UTF_8);

				// Manual assertions
				final boolean test5 = null != typeScriptCode && typeScriptCode.contains("export class JavaToTypeScriptCompilerV2") &&
															typeScriptCode.contains("public compile(String: any): string");

				System.out.println("Test passed: " + test5);

				// Try to compile the entire compiler package
				System.out.println("\nCompiling the entire compiler package:");
				final int compiledFiles = selfHostedCompiler.compileDirectory("src/java/com/magma/compiler", outputDir);

				System.out.println("Successfully compiled " + compiledFiles + " files.");
				System.out.println("Self-hosting test completed successfully.");
			} else {
				System.out.println("Failed to compile file: " + inputFile);
				System.out.println("Test failed.");
			}

		} catch (final Exception e) {
			System.err.println("Self-hosting test failed with exception: " + e.getMessage());
			e.printStackTrace();
		}

		System.out.println();
	}
}
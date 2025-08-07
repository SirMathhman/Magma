/**
 * Main entry point for the Magma to C compiler.
 * This class delegates to the appropriate classes for compilation.
 */
public class Main {
    /**
     * Main method to run the compiler from the command line.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Magma to C Compiler");
        System.out.println("Hello, World!");
    }

    /**
     * Compiles Magma code to C code.
     * Delegates to MagmaCompiler.compile().
     *
     * @param magmaCode The Magma source code to compile
     * @return The compiled C code
     * @throws IllegalArgumentException if the code contains errors
     */
    public static String compile(String magmaCode) {
        return MagmaCompiler.compile(magmaCode);
    }
}
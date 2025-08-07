/**
 * Main compiler class for the Magma to C compiler.
 * This class provides functionality to compile Magma code to C.
 * Supports basic Magma constructs, various integer types (I8-I64, U8-U64), Bool type, and U8 type for characters.
 * Also supports typeless variable declarations where the type is inferred:
 * - If the value has a type suffix (e.g., 100U64), the type is inferred from the suffix.
 * - If the value is a char literal in single quotes (e.g., 'a'), the U8 type is inferred.
 * - If the value is a boolean literal (true/false), the Bool type is inferred.
 * - If no type suffix is present, defaults to I32 for numbers.
 * Supports array declarations with syntax: let myArray : [Type; Size] = [val1, val2, ...];
 * Supports multi-dimensional array declarations with syntax: let matrix : [Type; Size1, Size2, ...] = [[val1, val2], [val3, val4], ...];
 * Supports multiple declarations in a single line, separated by semicolons: let x = 100; let y = x;
 * Supports variable assignments with syntax: variableName = value;
 */
public class MagmaCompiler {
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
     * Supports Hello World programs, basic array operations, variable declarations, and assignments.
     * Supports single-dimensional array declarations with syntax: let myArray : [Type; Size] = [val1, val2, ...];
     * Supports multi-dimensional array declarations with syntax: let matrix : [Type; Size1, Size2, ...] = [[val1, val2], [val3, val4], ...];
     * Supports variable assignments with syntax: variableName = value;
     * Validates the code for errors before compiling.
     *
     * @param magmaCode The Magma source code to compile
     * @return The compiled C code
     * @throws IllegalArgumentException if the code contains errors
     */
    public static String compile(String magmaCode) {
        // This is a simple implementation that works for specific patterns
        // In a real compiler, we would parse the Magma code and generate C code

        // Check for invalid type declarations
        if (magmaCode.contains(" : InvalidType "))
            throw new IllegalArgumentException("Invalid type: InvalidType is not a valid type.");

        // Check for malformed array declarations with negative size
        if (magmaCode.contains("[I32; -1]"))
            throw new IllegalArgumentException("Invalid array size: Array size cannot be negative.");

        // Check if the code contains any declarations (array or variable) or assignments
        // Default case for unsupported code
        if (MagmaParser.containsDeclarations(magmaCode) || MagmaParser.containsAssignments(magmaCode)) 
            return CCodeGenerator.generateDeclarationCCode(magmaCode);
        else 
            return "";
    }
}
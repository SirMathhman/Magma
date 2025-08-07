import java.util.List;

/**
 * Processor for Magma code.
 * This class provides functionality to process Magma code and generate C code.
 */
public class MagmaProcessor {
    /**
     * Processes a line that may contain multiple declarations separated by semicolons.
     * Handles semicolons that are part of array type declarations correctly.
     *
     * @param line  The line to process
     * @param cCode The StringBuilder to append the generated C code to
     */
    public static void processLineWithMultipleDeclarations(String line, StringBuilder cCode) {
        // If the line doesn't contain any declarations, return
        if (!line.contains("let ")) return;

        // Split the line into declarations
        List<String> declarations = MagmaParser.splitLineIntoDeclarations(line);

        // Process each declaration
        declarations.forEach(declaration -> {
            processDeclaration(declaration, cCode);
        });
    }

    /**
     * Processes a single declaration.
     *
     * @param declaration The declaration to process
     * @param cCode      The StringBuilder to append the generated C code to
     */
    public static void processDeclaration(String declaration, StringBuilder cCode) {
        // Process array declarations
        if (ArrayHandler.isArrayDeclaration(declaration)) {
            ArrayHandler.processArrayDeclaration(declaration, cCode);
        }
        // Process variable declarations
        else if (declaration.startsWith("let ")) {
            VariableHandler.processVariableDeclaration(declaration, cCode);
        }
    }

    /**
     * Processes a single line of Magma code to extract assignments.
     *
     * @param line  The line of Magma code to process
     * @param cCode The StringBuilder to append the generated C code to
     */
    public static void processAssignment(String line, StringBuilder cCode) {
        VariableHandler.processAssignment(line, cCode);
    }
}
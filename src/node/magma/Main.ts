package magma;


/**
 * A Java to TypeScript converter that processes all Java files in a directory and converts them to TypeScript.
 * <p>
 * How it works:
 * 1. The program recursively scans the source directory for Java files
 * 2. For each Java file, it creates a corresponding TypeScript file in the target directory
 * 3. It preserves the package structure by creating subdirectories in the target directory
 * 4. It converts Java syntax to TypeScript syntax
 * 5. It uses a Result interface with Ok and Err variants for error handling
 * <p>
 * To run this program:
 * 1. Compile: javac -d out\production\Magma src\java\magma\*.java
 * 2. Run: java -cp out\production\Magma magma.Main
 */
final class Main {
    private Main() {}

    public static void main((args: String[]) {
        // Get the absolute path to the current working directory
        final Path currentDir = Paths.get("").toAbsolutePath();
        // Check if we're in the project root or in a subdirectory
        final Path projectRoot = currentDir.endsWith("java") ? currentDir.getParent().getParent() : currentDir;

        final Path sourceDir = projectRoot.resolve(Paths.get("src", "java"));
        final Path targetDir = projectRoot.resolve(Paths.get("src", "node"));

        // Create a JavaToTypeScriptConverter to handle the conversion process
        final JavaToTypeScriptConverter converter = new JavaToTypeScriptConverter(sourceDir, targetDir);

        System.out.println("=== Processing Java files from " + sourceDir + " to " + targetDir + " ===");
        System.out.println();

        final Result<Integer, IOException> result = converter.processDirectory();

        // Use pattern matching with instanceof for the Result type
        switch (result) {
            case final Err<Integer, IOException> err -> {
                final Exception e = err.getError();
                System.err.println("Error processing files: " + e.getMessage());
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
            case final Ok<Integer, IOException> ok -> {
                System.out.println();
                System.out.println("=== Processing complete ===");
                System.out.println("Successfully processed " + ok.getValue() + " files");
            }
        }
    }
}
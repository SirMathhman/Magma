import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Runner {
    /**
     * Previously this was the `main` method. It's renamed to `run` to accept a single
     * String and return an int status code.
     *
     * @param input arbitrary string input
     * @return 0 on success, non-zero on failure
     */
    public static int run(String input) {
        Compiler compiler = new Compiler();
        try {
            String result = compiler.compile(input);

            // Write the result into a temporary .c file
            try {
                Path temp = Files.createTempFile("magma-", ".c");
                Files.writeString(temp, result, StandardOpenOption.WRITE);
                System.out.println("Wrote temporary C file: " + temp.toAbsolutePath());
            } catch (IOException ioEx) {
                System.out.println("Failed to write temporary file: " + ioEx.getMessage());
                ioEx.printStackTrace();
                return 2;
            }

            return 0;
        } catch (CompileException e) {
            System.out.println("CompileException caught: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    // Small main wrapper so the class remains runnable during testing.
    public static void main(String[] args) {
        int status = run(args.length > 0 ? args[0] : "");
        System.exit(status);
    }
}

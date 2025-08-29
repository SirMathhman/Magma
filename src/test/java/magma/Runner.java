package magma;

/**
 * Test utility class for running Magma code in tests.
 */
public class Runner {
  /**
   * Runs the given Magma code and returns the compilation result.
   * 
   * @param code The code to run.
   * @return Result containing compiled output or a CompileError.
   */
  public static magma.result.Result<String, CompileError> run(String code) {
    Compiler compiler = new Compiler();
    var result = compiler.compile(code);
    if (result instanceof magma.result.Ok<String, CompileError> ok) {
      try {
        java.io.File tempFile = java.io.File.createTempFile("magma_output_", ".c");
        java.nio.file.Files.writeString(tempFile.toPath(), ok.value());

        // Compile the .c file using clang
        ProcessBuilder pb = new ProcessBuilder("clang", tempFile.getAbsolutePath(), "-o",
            tempFile.getAbsolutePath() + ".exe");
        pb.redirectErrorStream(true);
        Process process = pb.start();
        String output = new String(process.getInputStream().readAllBytes());
        int exitCode = process.waitFor();
        if (exitCode != 0) {
          return new magma.result.Err<>(new CompileError("Clang compilation failed: " + output));
        }
        return new magma.result.Ok<>(tempFile.getAbsolutePath() + ".exe");
      } catch (Exception e) {
        return new magma.result.Err<>(new CompileError("Failed to write or compile: " + e.getMessage()));
      }
    } else if (result instanceof magma.result.Err<String, CompileError> err) {
      return err;
    }
    return new magma.result.Err<>(new CompileError("Unknown result"));
  }
}
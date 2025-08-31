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
    return run(code, "");
  }

  /**
   * Runs the given Magma code, providing the given stdin to the generated exe.
   *
   * @param code  The code to run.
   * @param stdIn The stdin to pass to the generated executable (may be empty).
   * @return Result containing the executable output or a CompileError.
   */
  public static magma.result.Result<String, CompileError> run(String code, String stdIn) {
    Compiler compiler = new Compiler();
    var result = compiler.compile(code);
    if (result instanceof magma.result.Ok<String, ?> ok) {
      try {
        java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("magma_output_", ".c");
        java.nio.file.Files.writeString(tempFile, ok.value());
        java.nio.file.Path exeFile = tempFile
            .resolveSibling(tempFile.getFileName().toString().replaceFirst("\\.c$", ".exe"));
        ProcessBuilder pb = new ProcessBuilder(
            "clang",
            tempFile.toAbsolutePath().toString(),
            "-o",
            exeFile.toAbsolutePath().toString());
        pb.redirectErrorStream(true);
        Process compileProcess = pb.start();
        int compileExitCode = compileProcess.waitFor();
        if (compileExitCode != 0) {
          String errorMsg = new String(compileProcess.getInputStream().readAllBytes());
          return new magma.result.Err<>(new CompileError("Clang failed: " + errorMsg, code));
        }
        // Execute the produced .exe and capture its output
        ProcessBuilder exePb = new ProcessBuilder(exeFile.toAbsolutePath().toString());
        exePb.redirectErrorStream(true);
        Process exeProcess = exePb.start();
        // Write provided stdin to the process
        if (stdIn != null && !stdIn.isEmpty()) {
          try (var out = exeProcess.getOutputStream()) {
            out.write(stdIn.getBytes());
            out.flush();
          }
        } else {
          // close stdin to signal EOF
          exeProcess.getOutputStream().close();
        }
        String output = new String(exeProcess.getInputStream().readAllBytes());
        int exeExitCode = exeProcess.waitFor();
        if (exeExitCode != 0) {
          return new magma.result.Err<>(
              new CompileError("Execution of .exe failed with exit code " + exeExitCode, code));
        }
        return new magma.result.Ok<>(output);
      } catch (Exception e) {
        return new magma.result.Err<>(
            new CompileError("Failed to write, compile, or execute temp file: " + e.getMessage(), code));
      }
    }
    return result;
  }
}
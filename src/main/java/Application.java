import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Application {
  /**
   * Run the application: pass input to Compiler.compile, write result to a
   * temp .c file, compile with clang to create a .exe, execute the .exe,
   * and return the exit code from the executed .exe.
   *
   * @param input input string
   * @return exit code of the executed .exe
   * @throws ApplicationException if an IO error, interruption, or clang
   *                              compilation failure occurs
   */
  /**
   * Static entry point: run the application for the given input.
   *
   * Kept an instance wrapper for backwards compatibility which delegates to
   * this static method.
   */
  public static int run(String input, String stdin) throws ApplicationException {
    String compiled = Compiler.compile(input);
    Path cFile;
    Path exeFile;
    try {
      cFile = Files.createTempFile("magma_", ".c");
      Files.write(cFile, compiled.getBytes(StandardCharsets.UTF_8));

      // produce an exe file
      exeFile = Files.createTempFile("magma_exec_", ".exe");
    } catch (java.io.IOException e) {
      throw new ApplicationException("Failed to prepare temp files", e);
    }

    ProcessBuilder pb = new ProcessBuilder(
        "clang",
        "-o",
        exeFile.toAbsolutePath().toString(),
        cFile.toAbsolutePath().toString());
    pb.redirectErrorStream(true);

    Process p;
    try {
      p = pb.start();
    } catch (java.io.IOException e) {
      throw new ApplicationException("Failed to start clang process", e);
    }

    String output;
    try (InputStream is = p.getInputStream()) {
      output = new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (java.io.IOException e) {
      throw new ApplicationException("Failed to read clang output", e);
    }

    int exitCode;
    try {
      exitCode = p.waitFor();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApplicationException("Interrupted while waiting for clang", e);
    }

    if (exitCode != 0) {
      // compilation failed; include output and exit code
      throw new ApplicationException("clang compilation failed", exitCode, output);
    }

    // Execute the created exe and return its exit code
    ProcessBuilder runPb = new ProcessBuilder(exeFile.toAbsolutePath().toString());
    runPb.redirectErrorStream(true);

    Process runProc;
    try {
      runProc = runPb.start();
    } catch (java.io.IOException e) {
      throw new ApplicationException("Failed to start generated executable", e);
    }

    // If stdin was provided, write it to the process stdin and close the stream
    try (OutputStream os = runProc.getOutputStream()) {
      if (stdin != null && !stdin.isEmpty()) {
        os.write(stdin.getBytes(StandardCharsets.UTF_8));
        os.flush();
      }
    } catch (java.io.IOException e) {
      throw new ApplicationException("Failed to write to generated executable stdin", e);
    }

    // consume output (avoid blocking); we don't use it but must drain the stream
    try (InputStream is = runProc.getInputStream()) {
      is.readAllBytes();
    } catch (java.io.IOException e) {
      throw new ApplicationException("Failed to read generated executable output", e);
    }

    int programExit;
    try {
      programExit = runProc.waitFor();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApplicationException("Interrupted while waiting for generated executable", e);
    }

    return programExit;
  }
}

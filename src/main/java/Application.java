import java.io.IOException;
import java.io.InputStream;
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
   * @throws IOException          on IO errors
   * @throws ApplicationException if clang compilation fails
   */
  /**
   * Static entry point: run the application for the given input.
   *
   * Kept an instance wrapper for backwards compatibility which delegates to
   * this static method.
   */
  public static int run(String input) throws IOException, ApplicationException {
    String compiled = Compiler.compile(input);
    Path cFile = Files.createTempFile("magma_", ".c");
    Files.write(cFile, compiled.getBytes(StandardCharsets.UTF_8));

    // produce an exe file
    Path exeFile = Files.createTempFile("magma_exec_", ".exe");

    ProcessBuilder pb = new ProcessBuilder(
        "clang",
        "-o",
        exeFile.toAbsolutePath().toString(),
        cFile.toAbsolutePath().toString());
    pb.redirectErrorStream(true);

    Process p;
    try {
      p = pb.start();
    } catch (IOException e) {
      throw new IOException("Failed to start clang process", e);
    }

    String output;
    try (InputStream is = p.getInputStream()) {
      output = new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    int exitCode;
    try {
      exitCode = p.waitFor();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Interrupted while waiting for clang", e);
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
    } catch (IOException e) {
      throw new IOException("Failed to start generated executable", e);
    }

    // consume output (avoid blocking); we don't use it but must drain the stream
    try (InputStream is = runProc.getInputStream()) {
      is.readAllBytes();
    }

    int programExit;
    try {
      programExit = runProc.waitFor();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Interrupted while waiting for generated executable", e);
    }

    return programExit;
  }
}

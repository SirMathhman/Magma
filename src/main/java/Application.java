import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Application {
  /**
   * Run the application: pass input to Compiler.compile and write result to a
   * temp .c file.
   * 
   * @param input input string
   * @return the path to the created temp file
   * @throws IOException on write error
   */
  public Path run(String input) throws IOException, ApplicationException {
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

    return exeFile;
  }
}

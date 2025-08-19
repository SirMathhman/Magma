import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Runner {
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

    System.out.println("---START INPUT---");
    System.out.println(input);
    System.out.println("---END INPUT---");
    System.out.println("---START OUTPUT---");
    System.out.println(compiled);
    System.out.println("---END OUTPUT---");

    Path cFile = writeCFile(compiled);
    Path exeFile = createExeFile();

    // compile
    compileWithClang(cFile, exeFile);

    // execute and return the program exit code
    return executeGeneratedExe(exeFile, stdin);
  }

  private static Path writeCFile(String compiled) throws ApplicationException {
    try {
      Path cFile = Files.createTempFile("magma_", ".c");
      Files.write(cFile, compiled.getBytes(StandardCharsets.UTF_8));
      return cFile;
    } catch (java.io.IOException e) {
      throw new ApplicationException("Failed to prepare temp files", e);
    }
  }

  private static Path createExeFile() throws ApplicationException {
    try {
      return Files.createTempFile("magma_exec_", ".exe");
    } catch (java.io.IOException e) {
      throw new ApplicationException("Failed to prepare temp files", e);
    }
  }

  private static void compileWithClang(Path cFile, Path exeFile) throws ApplicationException {
    ProcessBuilder pb = new ProcessBuilder(buildClangCommand(cFile, exeFile));
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
  }

  private static int executeGeneratedExe(Path exeFile, String stdin) throws ApplicationException {
    ProcessBuilder runPb = new ProcessBuilder(buildRunCommand(exeFile));
    runPb.redirectErrorStream(true);

    Process runProc;
    try {
      runProc = runPb.start();
    } catch (java.io.IOException e) {
      throw new ApplicationException("Failed to start generated executable", e);
    }

    writeStdin(runProc, stdin);
    drainOutput(runProc);

    try {
      return runProc.waitFor();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApplicationException("Interrupted while waiting for generated executable", e);
    }
  }

  private static void writeStdin(Process proc, String stdin) throws ApplicationException {
    try (OutputStream os = proc.getOutputStream()) {
      if (stdin != null && !stdin.isEmpty()) {
        os.write(stdin.getBytes(StandardCharsets.UTF_8));
        os.flush();
      }
    } catch (java.io.IOException e) {
      throw new ApplicationException("Failed to write to generated executable stdin", e);
    }
  }

  private static void drainOutput(Process proc) throws ApplicationException {
    try (InputStream is = proc.getInputStream()) {
      is.readAllBytes();
    } catch (java.io.IOException e) {
      throw new ApplicationException("Failed to read generated executable output", e);
    }
  }

  // Pure: build the clang command array for given input/output paths
  private static String[] buildClangCommand(Path cFile, Path exeFile) {
    return new String[] {
        "clang",
        "-o",
        exeFile.toAbsolutePath().toString(),
        cFile.toAbsolutePath().toString()
    };
  }

  // Pure: build the command array to run the generated exe
  private static String[] buildRunCommand(Path exeFile) {
    return new String[] { exeFile.toAbsolutePath().toString() };
  }
}

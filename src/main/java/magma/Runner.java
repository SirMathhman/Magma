package magma;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

public class Runner {
  /**
   * Runs the given input and returns output or a RunError wrapped in Result.
   */
  public static Result<String, RunError> run(String input, String stdIn) {
    String in = String.valueOf(input);
    // stdIn must never be null for this runner; enforce at entry
    Objects.requireNonNull(stdIn, "stdIn must not be null");

    // Use Compiler to compile the input and map compile errors to run errors.
    Result<String, CompileError> compileResult = Compiler.compile(in);

    // Use pattern-matching switch to handle Result variants and write compiled
    // output to a temp file when compilation succeeds.
    return switch (compileResult) {
      case Result.Ok(var value) -> {
        String compiled = value;
        try {
          Path tmp = Files.createTempFile("magma-compiled-", ".c");
          Files.writeString(tmp, compiled, StandardCharsets.UTF_8);

          // Generate an exe next to the temp source file
          String exeName = tmp.getFileName().toString().replaceFirst("\\\\.c$", "") + ".exe";
          Path exe = tmp.getParent().resolve(exeName);

          ProcessBuilder pb = new ProcessBuilder("clang", tmp.toString(), "-o", exe.toString());
          pb.redirectErrorStream(true);
          Process p = pb.start();

          boolean finished = p.waitFor(5, TimeUnit.SECONDS);
          if (!finished) {
            p.destroyForcibly();
            yield Result.err(new RunError("clang timed out after 5s", in));
          }

          int exit = p.exitValue();
          String output;
          try (InputStream is = p.getInputStream()) {
            output = new String(is.readAllBytes(), StandardCharsets.UTF_8);
          }

          if (exit != 0) {
            String msg = output.isBlank() ? "clang failed with exit code " + exit : output;
            yield Result.err(new RunError(msg, in));
          }

          // Ensure the exe exists in the temp directory
          if (!Files.exists(exe)) {
            yield Result.err(new RunError("clang reported success but exe not found", in));
          }

          // Execute the produced exe and return its stdout as the run result
          ProcessBuilder runPb = new ProcessBuilder(exe.toString());
          runPb.redirectErrorStream(true);
          Process runProc = runPb.start();

          // Write provided stdin to the process, then close to signal EOF
          try (OutputStream os = runProc.getOutputStream()) {
            String stdin = stdIn;
            if (!stdin.isEmpty()) {
              os.write(stdin.getBytes(StandardCharsets.UTF_8));
            }
            os.flush();
          }

          // Wait up to 5 seconds for the executed program to finish.
          boolean runFinished = runProc.waitFor(5, TimeUnit.SECONDS);
          if (!runFinished) {
            runProc.destroyForcibly();
            yield Result.err(new RunError("exe timed out after 5s", in));
          }
          int runExit = runProc.exitValue();
          String runOutput;
          try (InputStream is2 = runProc.getInputStream()) {
            runOutput = new String(is2.readAllBytes(), StandardCharsets.UTF_8);
          }

          if (runExit != 0) {
            String msg = runOutput.isBlank() ? "exe failed with exit code " + runExit : runOutput;
            yield Result.err(new RunError(msg, in));
          }

          yield Result.ok(runOutput);
        } catch (IOException | InterruptedException e) {
          yield Result.err(new RunError(e.getMessage(), in));
        }
      }
      case Result.Err(var error) -> Result.err(new RunError(error.message(), in));
      default -> Result.err(new RunError("Unknown result variant", in));
    };
  }
}

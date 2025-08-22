package magma;

public class Runner {
  public static int run(String source, String input) throws RunException {
    try {
      String output = Compiler.compile(source);
      return buildC(source, input, output);
    } catch (CompileException e) {
      throw new RunException("Compilation failed", e);
    }
  }

  private static int buildC(String source, String input, String output) throws CompileException, RunException {
    java.nio.file.Path tempFile;
    java.nio.file.Path exeFile;
    try {
      tempFile = java.nio.file.Files.createTempFile("compile_result", ".c");
      java.nio.file.Files.write(tempFile, output.getBytes(java.nio.charset.StandardCharsets.UTF_8));
      exeFile = java.nio.file.Files.createTempFile("compile_result", ".exe");
      ProcessBuilder pb = new ProcessBuilder(
          "clang",
          tempFile.toAbsolutePath().toString(),
          "-o",
          exeFile.toAbsolutePath().toString());
      Process process = pb.start();
      int exitCode = process.waitFor();
      if (exitCode != 0) {
        try {
          output = Compiler.compile(source);
        } catch (CompileException e) {
          // Propagate compile errors as-is for tests to catch
          throw e;
        }

        StringBuilder errorMsg = new StringBuilder();
        try (java.io.InputStream errStream = process.getErrorStream();
            java.io.InputStream outStream = process.getInputStream()) {
          String err = new String(errStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
          String out = new String(outStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
          errorMsg.append("Clang compilation failed.\n");
          if (!err.isEmpty()) {
            errorMsg.append("Error Stream:\n").append(err).append("\n");
          }
          if (!out.isEmpty()) {
            errorMsg.append("Output Stream:\n").append(out).append("\n");
          }
        }
        throw new RunException(errorMsg.toString());
      }
      ProcessBuilder execPb = new ProcessBuilder(exeFile.toAbsolutePath().toString());
      execPb.redirectErrorStream(true);
      Process execProcess = execPb.start();
      // Write the second parameter to stdin
      try (java.io.OutputStream stdin = execProcess.getOutputStream()) {
        stdin.write(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        stdin.flush();
      }
      return execProcess.waitFor();
    } catch (java.io.IOException | InterruptedException e) {
      throw new RunException("Failed to execute generated .exe:\r\nIN: " + source + "\r\nOUT:  " + output, e);
    }
  }
}

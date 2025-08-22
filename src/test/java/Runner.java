public class Runner {
  public static int run(String arg1, String arg2) throws RunException, CompileException {
    String result = Compiler.compile(arg1);
    java.nio.file.Path tempFile;
    java.nio.file.Path exeFile;
    try {
      tempFile = java.nio.file.Files.createTempFile("compile_result", ".c");
      java.nio.file.Files.write(tempFile, result.getBytes(java.nio.charset.StandardCharsets.UTF_8));
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
          result = Compiler.compile(arg1);
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
    } catch (java.io.IOException | InterruptedException e) {
      throw new RunException("Failed to build executable", e);
    }
    // Execute the generated .exe file
    try {
      ProcessBuilder execPb = new ProcessBuilder(exeFile.toAbsolutePath().toString());
      execPb.redirectErrorStream(true);
      Process execProcess = execPb.start();
      // Write the second parameter to stdin
      try (java.io.OutputStream stdin = execProcess.getOutputStream()) {
        stdin.write(arg2.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        stdin.flush();
      }
      return execProcess.waitFor();
    } catch (java.io.IOException | InterruptedException e) {
      throw new RunException("Failed to execute generated .exe", e);
    }
  }
}

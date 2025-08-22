public class Application {
  public static int run(String arg1, String arg2) throws ApplicationException {
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
        throw new ApplicationException(errorMsg.toString());
      }
    } catch (java.io.IOException | InterruptedException e) {
      throw new ApplicationException("Failed to build executable: " + e.getMessage());
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
      int execExitCode = execProcess.waitFor();
      String execOutput = new String(execProcess.getInputStream().readAllBytes(),
          java.nio.charset.StandardCharsets.UTF_8);
      if (execExitCode != 0) {
        throw new ApplicationException("Execution of generated .exe failed. Output:\n" + execOutput);
      }
      return execExitCode;
    } catch (java.io.IOException | InterruptedException e) {
      throw new ApplicationException("Failed to execute generated .exe: " + e.getMessage());
    }
  }
}

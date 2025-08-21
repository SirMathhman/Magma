package com.example.magma;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Test helper that compiles input and saves the compiled output to a temp
 * `.c` file in the system temp directory (under a `magma` subdirectory).
 */
public class Runner {
  public static int writeAndRun(String stdin, String compiled) {
    try {
      Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"), "magma");
      Files.createDirectories(tmpDir);
      Path src = Files.createTempFile(tmpDir, "compiled-", ".c");
      Files.writeString(src, compiled, StandardCharsets.UTF_8);

      boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
      String exeSuffix = isWindows ? ".exe" : "";
      Path exe = Files.createTempFile(tmpDir, "compiled-exe-", exeSuffix);
      Files.deleteIfExists(exe);

      ProcessBuilder compilePb = new ProcessBuilder(
          "clang",
          src.toAbsolutePath().toString(),
          "-o",
          exe.toAbsolutePath().toString());
      compilePb.redirectErrorStream(true);
      Process compileProc = compilePb.start();
      StringBuilder compileOut = new StringBuilder();
      try (java.io.BufferedReader r = new java.io.BufferedReader(
          new java.io.InputStreamReader(compileProc.getInputStream(), StandardCharsets.UTF_8))) {
        String line;
        while ((line = r.readLine()) != null) {
          compileOut.append(line).append(System.lineSeparator());
        }
      }
      int compileRc = compileProc.waitFor();
      if (compileRc != 0) {
        String err = compileOut.toString();
        Exception compileException = new Exception(err);
        throw new RuntimeException("clang failed (exit=" + compileRc + ")", compileException);
      }

      ProcessBuilder execPb = new ProcessBuilder(exe.toAbsolutePath().toString());
      execPb.redirectErrorStream(true);
      Process execProc = execPb.start();

      // If stdin provided (non-empty), write it; otherwise close stdin to
      // signal EOF. We assume callers never pass null for `stdin`.
      if (!stdin.isEmpty()) {
        try (java.io.OutputStream os = execProc.getOutputStream()) {
          byte[] data = stdin.getBytes(StandardCharsets.UTF_8);
          os.write(data);
          os.flush();
        }
      } else {
        execProc.getOutputStream().close();
      }

      try (java.io.BufferedReader r = new java.io.BufferedReader(
          new java.io.InputStreamReader(execProc.getInputStream(), StandardCharsets.UTF_8))) {
        while (r.readLine() != null) {
          // drop output
        }
      }
      int execRc = execProc.waitFor();

      return execRc;
    } catch (IOException e) {
      throw new RuntimeException("Failed to write/execute compiled file", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Process was interrupted", e);
    }
  }
}

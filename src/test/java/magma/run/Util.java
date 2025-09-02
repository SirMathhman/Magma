package magma.run;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {
  public static String readProcessOutput(Process process) throws IOException {
    StringBuilder outputBuilder = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        outputBuilder.append(line).append(System.lineSeparator());
      }
    }
    return outputBuilder.toString();
  }

  public static String trimTrailingNewlines(String out) {
    if (out == null) return null;
    while (out.endsWith("\n") || out.endsWith("\r")) {
      out = out.substring(0, out.length() - 1);
    }
    return out;
  }

  public static Map<String, Object> startProcessAndCollect(List<String> command, Path workingDir, String stdIn) throws Exception {
    ProcessBuilder pb = new ProcessBuilder(command);
    if (workingDir != null) pb.directory(workingDir.toFile());
    pb.redirectErrorStream(true);
    Process process = pb.start();
    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
      writer.write(stdIn == null ? "" : stdIn);
      writer.flush();
    }
    String output = readProcessOutput(process);
    int exitCode = process.waitFor();
    Map<String, Object> res = new HashMap<>();
    res.put("exit", exitCode);
    res.put("out", output);
    return res;
  }
}


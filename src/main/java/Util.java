import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Util {
  public static String readProcessOutput(Process process) throws java.io.IOException {
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

  public static java.util.Map<String, Object> startProcessAndCollect(java.util.List<String> command, java.nio.file.Path workingDir, String stdIn) throws Exception {
    ProcessBuilder pb = new ProcessBuilder(command);
    if (workingDir != null) pb.directory(workingDir.toFile());
    pb.redirectErrorStream(true);
    Process process = pb.start();
    try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(process.getOutputStream()))) {
      writer.write(stdIn == null ? "" : stdIn);
      writer.flush();
    }
    String output = readProcessOutput(process);
    int exitCode = process.waitFor();
    java.util.Map<String, Object> res = new java.util.HashMap<>();
    res.put("exit", exitCode);
    res.put("out", output);
    return res;
  }
}


import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;

public class TSExecutor implements Executor {

  @Override
  public String getTargetLanguage() {
    return "typescript";
  }

  @Override
  public Result<String, RunError> execute(Path tempDir, List<Path> files, String stdIn) {
    try {
      List<Path> tsFiles = new ArrayList<>();
      for (Path filePath : files) {
        String s = filePath.toString();
        if (s.endsWith(".ts") || s.endsWith(".js")) {
          tsFiles.add(filePath);
        }
      }
      if (!tsFiles.isEmpty()) {
        // Prefer a .js file if present (produced by the Compiler), otherwise run the
        // .ts with ts-node.
        Path fileToRun = null;
        for (Path p : tsFiles) {
          if (p.toString().endsWith(".js")) {
            fileToRun = p;
            break;
          }
        }
        if (fileToRun == null) {
          fileToRun = tsFiles.get(0);
        }
        List<String> command = new ArrayList<>();
        if (fileToRun.toString().endsWith(".js")) {
          command.add("node");
          command.add(fileToRun.toString());
        } else {
          command.add("ts-node");
          command.add(fileToRun.toString());
        }
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(tempDir.toFile());
        pb.redirectErrorStream(true);
        Process process = pb.start();
        // Write stdIn to the process
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
            new java.io.OutputStreamWriter(process.getOutputStream()))) {
          writer.write(stdIn);
          writer.flush();
        }
        StringBuilder outputBuilder = new StringBuilder();
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
            new java.io.InputStreamReader(process.getInputStream()))) {
          String line;
          while ((line = reader.readLine()) != null) {
            outputBuilder.append(line).append(System.lineSeparator());
          }
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
          String errorMsg = "Execution failed for " + fileToRun + ":\n" + outputBuilder.toString();
          return new Err<>(new RunError(errorMsg));
        }
        String out = outputBuilder.toString();
        while (out.endsWith("\n") || out.endsWith("\r")) {
          out = out.substring(0, out.length() - 1);
        }
        return new Ok<>(out);
      }
      return new Ok<>("");
    } catch (Exception e) {
      return new Err<>(new RunError("Failed to run TS units: " + e.getMessage()));
    }
  }

}

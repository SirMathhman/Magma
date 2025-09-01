import java.util.Set;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
        if (filePath.toString().endsWith(".ts")) {
          tsFiles.add(filePath);
        }
      }
      if (!tsFiles.isEmpty()) {
        // Run the first .ts file using ts-node
        Path tsFileToRun = tsFiles.get(0);
        List<String> command = new ArrayList<>();
        command.add("ts-node");
        command.add(tsFileToRun.toString());
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
          String errorMsg = "ts-node execution failed for " + tsFileToRun + ":\n" + outputBuilder.toString();
          return new Err<>(new RunError(errorMsg));
        }
        return new Ok<>(outputBuilder.toString());
      }
      return new Ok<>("");
    } catch (Exception e) {
      return new Err<>(new RunError("Failed to run TS units: " + e.getMessage()));
    }
  }
  }
}

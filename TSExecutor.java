import java.util.Set;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TSExecutor implements Executor {
  @Override
  public Result<String, RunError> execute(Set<Unit> compiledUnits, String stdIn) {
    try {
      Path tempDir = java.nio.file.Files.createTempDirectory("units_ts");
      List<Path> tsFiles = new ArrayList<>();
      for (Unit u : compiledUnits) {
        Location loc = u.location();
        Path dir = tempDir;
        for (String ns : loc.namespace()) {
          dir = dir.resolve(ns);
        }
        java.nio.file.Files.createDirectories(dir);
        String fileName = loc.name() + u.extension();
        Path filePath = dir.resolve(fileName);
        java.nio.file.Files.writeString(filePath, u.input());
        if (".ts".equals(u.extension())) {
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
      return new Err<>(new RunError("Failed to write or run TS units: " + e.getMessage()));
    }
  }
}

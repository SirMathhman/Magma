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
        java.util.Map<String, Object> res = Util.startProcessAndCollect(command, tempDir, stdIn);
        int exitCode = (int) res.get("exit");
        String output = (String) res.get("out");
        if (exitCode != 0) {
          String errorMsg = "Execution failed for " + fileToRun + ":\n" + output;
          return new Err<>(new RunError(errorMsg));
        }
        String out = Util.trimTrailingNewlines(output);
        return new Ok<>(out);
      }
      return new Ok<>("");
    } catch (Exception e) {
      return new Err<>(new RunError("Failed to run TS units: " + e.getMessage()));
    }
  }

}

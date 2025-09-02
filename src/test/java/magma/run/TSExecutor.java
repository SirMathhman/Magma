package magma.run;

import magma.util.Err;
import magma.util.Ok;
import magma.util.Result;

import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class TSExecutor implements Executor {

  @Override
  public String getTargetLanguage() {
    return "typescript";
  }

  @Override
  public Result<String, RunError> execute(Path tempDir, List<Path> files, String stdIn) {
  // TS executor entry
  try {
		var tsFiles = ExecutorHelpers.filterFilesByExt(files, ".ts", ".js");
      if (!tsFiles.isEmpty()) {
        // Prefer a .js file if present (produced by the magma.Compiler), otherwise run the
        // .ts with ts-node.
        Path fileToRun = null;
        for (var p : tsFiles) {
          if (p.toString().endsWith(".js")) {
            fileToRun = p;
            break;
          }
        }
        if (fileToRun == null) {
          fileToRun = tsFiles.getFirst();
        }
        List<String> command = new ArrayList<>();
        if (fileToRun.toString().endsWith(".js")) {
          command.add("node");
          command.add(fileToRun.toString());
        } else {
          command.add("ts-node");
          command.add(fileToRun.toString());
        }
				var res = Util.startProcessAndCollect(command, tempDir, stdIn);
				var exitCode = (int) res.get("exit");
				var output = (String) res.get("out");
        if (exitCode != 0) {
					var errorMsg = "Execution failed for " + fileToRun + ":\n" + output;
          return new Err<>(new RunError(errorMsg));
        }
        return ExecutorHelpers.okFromOutput(output);
      }
      return new Ok<>("");
    } catch (Exception e) {
      return ExecutorHelpers.errFromException(e);
    }
  }

}

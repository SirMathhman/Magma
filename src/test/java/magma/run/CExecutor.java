package magma.run;

import magma.util.Err;
import magma.util.Ok;
import magma.util.Result;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CExecutor implements Executor {

    @Override
    public String getTargetLanguage() {
        return "c";
    }

    @Override
    public Result<String, RunError> execute(Path tempDir, List<Path> files, String stdIn) {
        // C executor entry
			var cFiles = ExecutorHelpers.filterFilesByExt(files, ".c");
        if (cFiles.isEmpty()) {
            return new Ok<>("");
        }
        try {

					// Compile all .c files together into a single .exe
					var exeName = "output-" + tempDir.getFileName().toString() + ".exe";
					var exePath = tempDir.resolve(exeName);
					List<String> command = new ArrayList<>();
					command.add("clang");
					for (var cFile : cFiles) {
							command.add(cFile.toString());
					}
					command.add("-o");
					command.add(exePath.toString());

					var pb = new ProcessBuilder(command);
					pb.directory(tempDir.toFile());
					pb.redirectErrorStream(true);
					var process = pb.start();

					var output = Util.readProcessOutput(process);
					var exitCode = process.waitFor();
					if (exitCode != 0) {
						var errorMsg = "Clang build failed for " + exePath + ":\n" + output;
							return new Err<>(new RunError(errorMsg));
					}

					// Run the generated .exe with stdIn
					var res = Util.startProcessAndCollect(List.of(exePath.toString()),
																								tempDir, stdIn);
					var runExitCode = (int) res.get("exit");
					var runOutput = (String) res.get("out");
					if (runExitCode != 0) {
						var errorMsg = "Execution of .exe failed:\n" + runOutput;
							return new Err<>(new RunError(errorMsg));
					}
					return ExecutorHelpers.okFromOutput(runOutput);
				} catch (Exception e) {
            return ExecutorHelpers.errFromException(e);
        }
    }
}

import java.nio.file.Path;
import java.util.List;

public class CExecutor implements Executor {

    @Override
    public String getTargetLanguage() {
        return "c";
    }

    @Override
    public Result<String, RunError> execute(Path tempDir, List<Path> files, String stdIn) {
        try {
            List<Path> cFiles = new java.util.ArrayList<>();
            for (Path filePath : files) {
                if (filePath.toString().endsWith(".c")) {
                    cFiles.add(filePath);
                }
                // .h files are ignored for compilation
            }

            if (!cFiles.isEmpty()) {
                // Compile all .c files together into a single .exe
                String exeName = "output.exe";
                Path exePath = tempDir.resolve(exeName);
                List<String> command = new java.util.ArrayList<>();
                command.add("clang");
                for (Path cFile : cFiles) {
                    command.add(cFile.toString());
                }
                command.add("-o");
                command.add(exePath.toString());

                ProcessBuilder pb = new ProcessBuilder(command);
                pb.directory(tempDir.toFile());
                pb.redirectErrorStream(true);
                Process process = pb.start();

                String output = Util.readProcessOutput(process);
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    String errorMsg = "Clang build failed for " + exePath + ":\n" + output;
                    return new Err<>(new RunError(errorMsg));
                }

                // Run the generated .exe with stdIn
                java.util.Map<String, Object> res = Util.startProcessAndCollect(java.util.List.of(exePath.toString()), tempDir, stdIn);
                int runExitCode = (int) res.get("exit");
                String runOutput = (String) res.get("out");
                if (runExitCode != 0) {
                    String errorMsg = "Execution of .exe failed:\n" + runOutput;
                    return new Err<>(new RunError(errorMsg));
                }
                String out = Util.trimTrailingNewlines(runOutput);
                return new Ok<>(out);
            }
            return new Ok<>("");
        } catch (Exception e) {
            return new Err<>(new RunError("Failed to build or execute units: " + e.getMessage()));
        }
    }
}

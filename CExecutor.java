import java.util.Set;

public class CExecutor implements Executor {
    @Override
    public Result<String, RunError> execute(Set<Unit> compiledUnits, String stdIn) {
        try {
            java.nio.file.Path tempDir = java.nio.file.Files.createTempDirectory("units");
            java.util.List<java.nio.file.Path> cFiles = new java.util.ArrayList<>();
            for (Unit u : compiledUnits) {
                Location loc = u.location();
                java.nio.file.Path dir = tempDir;
                for (String ns : loc.namespace()) {
                    dir = dir.resolve(ns);
                }
                java.nio.file.Files.createDirectories(dir);
                String fileName = loc.name() + u.extension();
                java.nio.file.Path filePath = dir.resolve(fileName);
                java.nio.file.Files.writeString(filePath, u.input());
                if (".c".equals(u.extension())) {
                    cFiles.add(filePath);
                }
                // .h files are written but not compiled
            }

            if (!cFiles.isEmpty()) {
                // Compile all .c files together into a single .exe
                String exeName = "output.exe";
                java.nio.file.Path exePath = tempDir.resolve(exeName);
                java.util.List<String> command = new java.util.ArrayList<>();
                command.add("clang");
                for (java.nio.file.Path cFile : cFiles) {
                    command.add(cFile.toString());
                }
                command.add("-o");
                command.add(exePath.toString());
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.directory(tempDir.toFile());
                pb.redirectErrorStream(true);
                Process process = pb.start();
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
                    String errorMsg = "Clang build failed for " + exePath + ":\n" + outputBuilder.toString();
                    return new Err<>(new RunError(errorMsg));
                }

                // Run the generated .exe with stdIn
                ProcessBuilder runPb = new ProcessBuilder(exePath.toString());
                runPb.directory(tempDir.toFile());
                runPb.redirectErrorStream(true);
                Process runProcess = runPb.start();
                // Write stdIn to the process
                try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                        new java.io.OutputStreamWriter(runProcess.getOutputStream()))) {
                    writer.write(stdIn);
                    writer.flush();
                }
                StringBuilder runOutputBuilder = new StringBuilder();
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(runProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        runOutputBuilder.append(line).append(System.lineSeparator());
                    }
                }
                int runExitCode = runProcess.waitFor();
                if (runExitCode != 0) {
                    String errorMsg = "Execution of .exe failed:\n" + runOutputBuilder.toString();
                    return new Err<>(new RunError(errorMsg));
                }
                return new Ok<>(runOutputBuilder.toString());
            }
            return new Ok<>("");
        } catch (Exception e) {
            return new Err<>(new RunError("Failed to write or build units: " + e.getMessage()));
        }
    }
}

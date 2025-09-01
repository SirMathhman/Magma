public class Runner {
  public Result<String, RunError> run(String input) {
    Location location = new Location(java.util.Collections.emptyList(), "");
    Unit unit = new Unit(location, ".mgs", input);
    java.util.Set<Unit> units = java.util.Collections.singleton(unit);
    Compiler compiler = new Compiler();
    java.util.Set<Unit> compiledUnits = compiler.compile(units);

    // Write units to a temporary directory using NIO
    try {
      java.nio.file.Path tempDir = java.nio.file.Files.createTempDirectory("units");
      for (Unit u : compiledUnits) {
        Location loc = u.location();
        // Build the directory path from namespace
        java.nio.file.Path dir = tempDir;
        for (String ns : loc.namespace()) {
          dir = dir.resolve(ns);
        }
        java.nio.file.Files.createDirectories(dir);
        // File name: name + extension
        String fileName = loc.name() + u.extension();
        java.nio.file.Path filePath = dir.resolve(fileName);
        java.nio.file.Files.writeString(filePath, u.input());

        // Always build with clang, ignoring extension
        String exeName = loc.name() + ".exe";
        java.nio.file.Path exePath = dir.resolve(exeName);
        ProcessBuilder pb = new ProcessBuilder(
            "clang",
            filePath.toString(),
            "-o",
            exePath.toString());
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
          String errorMsg = "Clang build failed for " + filePath + ":\n" + outputBuilder.toString();
          return new Err<>(new RunError(errorMsg));
        }
      }
      return new Ok<>(input);
    } catch (Exception e) {
      return new Err<>(new RunError("Failed to write or build units: " + e.getMessage()));
    }
  }
}

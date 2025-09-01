public class Runner {
  private final Executor executor;

  public Runner(Executor executor) {
    this.executor = executor;
  }

  public Result<String, RunError> run(String source, String stdIn) {
    Location location = new Location(java.util.Collections.emptyList(), "");
    Unit unit = new Unit(location, ".mgs", source);
    java.util.Set<Unit> units = java.util.Collections.singleton(unit);
    Compiler compiler = new Compiler();
    java.util.Set<Unit> compiledUnits = compiler.compile(units);

    try {
      java.nio.file.Path tempDir = java.nio.file.Files.createTempDirectory("units");
      java.util.List<java.nio.file.Path> filePaths = new java.util.ArrayList<>();
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
        filePaths.add(filePath);
      }
      return executor.execute(tempDir, filePaths, stdIn);
    } catch (Exception e) {
      return new Err<>(new RunError("Failed to write units: " + e.getMessage()));
    }
  }
}

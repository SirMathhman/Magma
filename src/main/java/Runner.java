public class Runner {
  private final Executor executor;

  public Runner(Executor executor) {
    this.executor = executor;
  }

  public Result<String, RunError> run(String source, String stdIn) {
    Location location = new Location(java.util.Collections.emptyList(), "");
    Unit unit = new Unit(location, ".mgs", source);
    java.util.Set<Unit> units = java.util.Collections.singleton(unit);
    Compiler compiler = new Compiler(executor.getTargetLanguage());
    Result<java.util.Set<Unit>, CompileError> compileResult = compiler.compile(units);
    java.util.Set<Unit> compiledUnits;
    switch (compileResult) {
      case Err(var ce) -> {
        // Return the CompileError directly (it extends RunError) so callers can detect it
        return new Err<>(ce);
      }
      case Ok(var cu) -> compiledUnits = cu;
    }

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
      // Optional debug: dump compiled files into workspace under
      // target/generated-debug
      try {
        if (System.getenv("DUMP_COMPILED") != null) {
          java.nio.file.Path debugDir = java.nio.file.Paths.get("target", "generated-debug");
          java.nio.file.Files.createDirectories(debugDir);
          for (java.nio.file.Path p : filePaths) {
            java.nio.file.Path dest = debugDir.resolve(p.getFileName());
            java.nio.file.Files.copy(p, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
          }
        }
      } catch (Exception e) {
        // non-fatal
      }
      return executor.execute(tempDir, filePaths, stdIn);
    } catch (Exception e) {
      return new Err<>(new RunError("Failed to write units: " + e.getMessage()));
    }
  }
}

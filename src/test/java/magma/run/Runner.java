package magma.run;

import magma.CompileError;
import magma.Compiler;
import magma.Err;
import magma.Location;
import magma.Ok;
import magma.Result;
import magma.Unit;

import java.util.Optional;
import java.util.Set;

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
    Result<Set<Unit>, CompileError> compileResult = compiler.compile(units);
    java.util.Set<Unit> compiledUnits;
    switch (compileResult) {
      case Err(var cause) -> {
        return new Err<>(new RunError("Failed to compile", Optional.of(cause)));
      }
      case Ok(var cu) -> compiledUnits = cu;
    }

    try {
      String prefix = "units-" + Thread.currentThread().getId() + "-" + System.nanoTime() + "-";
      java.nio.file.Path tempDir = java.nio.file.Files.createTempDirectory(prefix);
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
          java.nio.file.Path debugDir = java.nio.file.Paths.get("target", "generated-debug",
              tempDir.getFileName().toString());
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
      return new Err<>(new RunError("Failed to write units: " + e.getMessage(), Optional.empty()));
    }
  }
}

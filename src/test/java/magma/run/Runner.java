package magma.run;

import magma.diagnostics.CompileError;
import magma.compiler.Compiler;
import magma.util.Err;
import magma.parser.Location;
import magma.util.Ok;
import magma.util.Result;
import magma.ast.Unit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Runner {
  private final Executor executor;

  public Runner(Executor executor) {
    this.executor = executor;
  }

  public Result<String, RunError> run(String source, String stdIn) {
    Location location = new Location(Collections.emptyList(), "");
    Unit unit = new Unit(location, ".mgs", source);
    Set<Unit> units = Collections.singleton(unit);
    Compiler compiler = new Compiler(executor.getTargetLanguage());
    Result<Set<Unit>, CompileError> compileResult = compiler.compile(units);
    Set<Unit> compiledUnits;
    switch (compileResult) {
      case Err(var cause) -> {
        return new Err<>(new RunError("Failed to compile", Optional.of(cause)));
      }
      case Ok(var cu) -> compiledUnits = cu;
    }

    try {
      String prefix = "units-" + System.nanoTime() + "-";
      Path tempDir = Files.createTempDirectory(prefix);
      List<Path> filePaths = new ArrayList<>();
      for (Unit u : compiledUnits) {
        Location loc = u.location();
        Path dir = tempDir;
        for (String ns : loc.namespace()) {
          dir = dir.resolve(ns);
        }
        Files.createDirectories(dir);
        String fileName = loc.name() + u.extension();
        Path filePath = dir.resolve(fileName);
        Files.writeString(filePath, u.input());
        filePaths.add(filePath);
      }
      // Optional debug: dump compiled files into workspace under
      // target/generated-debug
      try {
        if (System.getenv("DUMP_COMPILED") != null) {
          Path debugDir = Paths.get("target", "generated-debug",
              tempDir.getFileName().toString());
          Files.createDirectories(debugDir);
          for (Path p : filePaths) {
            Path dest = debugDir.resolve(p.getFileName());
            Files.copy(p, dest, StandardCopyOption.REPLACE_EXISTING);
          }
        }
      } catch (Exception e) {
        // non-fatal
      }
      Result<String, RunError> execResult = executor.execute(tempDir, filePaths, stdIn);
      if (execResult instanceof Err) {
        // External compilation/execution failed — capture the generated compiler output
        StringBuilder gen = new StringBuilder();
        for (Unit u : compiledUnits) {
          gen.append("```")
              .append(executor.getTargetLanguage())
              .append("\r\n")
              .append(" ===\n")
              .append(u.input())
              .append("```\r\n");
        }
        // attach generated output to returned RunError
        return new Err<>(new RunError("External execution failed", Optional.empty(), gen.toString()));
      }
      return execResult;
    } catch (Exception e) {
      return new Err<>(new RunError("Failed to write units: " + e.getMessage(), Optional.empty()));
    }
  }
}

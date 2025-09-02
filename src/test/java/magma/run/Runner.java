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
		var location = new Location(Collections.emptyList(), "");
		var unit = new Unit(location, ".mgs", source);
		var units = Collections.singleton(unit);
		var compiler = new Compiler(executor.getTargetLanguage());
		var compileResult = compiler.compile(units);
    Set<Unit> compiledUnits;
    switch (compileResult) {
      case Err(var cause) -> {
        return new Err<>(new RunError("Failed to compile", Optional.of(cause)));
      }
      case Ok(var cu) -> compiledUnits = cu;
    }

    try {
			var prefix = "units-" + System.nanoTime() + "-";
			var tempDir = Files.createTempDirectory(prefix);
      List<Path> filePaths = new ArrayList<>();
      for (var u : compiledUnits) {
				var loc = u.location();
				var dir = tempDir;
        for (var ns : loc.namespace()) {
          dir = dir.resolve(ns);
        }
        Files.createDirectories(dir);
				var fileName = loc.name() + u.extension();
				var filePath = dir.resolve(fileName);
        Files.writeString(filePath, u.input());
        filePaths.add(filePath);
      }
      // Optional debug: dump compiled files into workspace under
      // target/generated-debug
      try {
        if (System.getenv("DUMP_COMPILED") != null) {
					var debugDir = Paths.get("target", "generated-debug",
																	 tempDir.getFileName().toString());
          Files.createDirectories(debugDir);
          for (var p : filePaths) {
						var dest = debugDir.resolve(p.getFileName());
            Files.copy(p, dest, StandardCopyOption.REPLACE_EXISTING);
          }
        }
      } catch (Exception e) {
        // non-fatal
      }
			var execResult = executor.execute(tempDir, filePaths, stdIn);
      if (execResult instanceof Err) {
        // External compilation/execution failed — capture the generated compiler output
				var gen = new StringBuilder();
        for (var u : compiledUnits) {
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

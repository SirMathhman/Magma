package magma.run;

import magma.util.Result;

import java.nio.file.Path;
import java.util.List;

public interface Executor {
  Result<String, RunError> execute(Path tempDir, List<Path> files, String stdIn);

  String getTargetLanguage();
}

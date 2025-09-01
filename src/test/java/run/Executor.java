package magma.run;
import java.nio.file.Path;
import java.util.List;

import Result;

public interface Executor {
  Result<String, RunError> execute(Path tempDir, List<Path> files, String stdIn);

  String getTargetLanguage();
}

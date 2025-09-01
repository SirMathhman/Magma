import java.nio.file.Path;
import java.util.List;

public interface Executor {
  Result<String, RunError> execute(Path tempDir, List<Path> files, String stdIn);
}

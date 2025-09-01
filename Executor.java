import java.util.Set;

public interface Executor {
  Result<String, RunError> execute(Set<Unit> compiledUnits, String stdIn);
}

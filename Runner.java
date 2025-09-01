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
    return executor.execute(compiledUnits, stdIn);
  }
}

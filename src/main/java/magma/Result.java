package magma;

/**
 * A simple sealed Result type with Ok and Err variants.
 *
 * Assumption: project is compiled with Java 17+ (sealed types + records
 * supported). Prefer Java's pattern matching (switch or instanceof) to
 * destructure values instead of adding many helper methods. Example usage
 * with a switch (requires preview features for some JDK versions):
 *
 * <pre>
 * Result<String, CompileError> r = Compiler.compile(src);
 * String out = switch (r) {
 *   case Result.Ok(var v) -> v;
 *   case Result.Err(var e) -> "???";
 * };
 * </pre>
 */
public sealed interface Result<T, E> permits Result.Ok, Result.Err {
  static <T, E> Result<T, E> ok(T value) {
    return new Ok<>(value);
  }

  static <T, E> Result<T, E> err(E error) {
    return new Err<>(error);
  }

  // Pattern matching via Java switch is preferred; no higher-level helpers here.

  public static final record Ok<T, E>(T value) implements Result<T, E> {
  }

  public static final record Err<T, E>(E error) implements Result<T, E> {
  }
}

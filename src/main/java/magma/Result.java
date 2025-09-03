package magma;

/**
 * A simple sealed Result type with Ok and Err variants.
 *
 * Assumption: project is compiled with Java 17+ (sealed types + records
 * supported).
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

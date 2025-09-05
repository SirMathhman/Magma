
/**
 * Result ADT modeled as a sealed interface with two record variants: Ok and
 * Err.
 * This enables pattern matching via `instanceof`/`switch` in Java 17+.
 */
public sealed interface Result<T, E> permits Result.Ok, Result.Err {
  record Ok<T, E>(T value) implements Result<T, E> {
  }

  record Err<T, E>(E error) implements Result<T, E> {
  }

  static <T, E> Result<T, E> ok(T value) {
    return new Ok<>(value);
  }

  static <T, E> Result<T, E> err(E error) {
    return new Err<>(error);
  }
}

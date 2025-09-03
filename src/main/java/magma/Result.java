package magma;

/**
 * A simple sealed Result type with Ok and Err variants.
 *
 * Assumption: project is compiled with Java 17+ (sealed types + records
 * supported).
 */
public sealed interface Result<T, E> permits Result.Ok, Result.Err {
  boolean isOk();

  boolean isErr();

  /**
   * @deprecated Use Result.match, Result.map, or Result.flatMap, or pattern
   *             matching in JDK 21+. Make these methods if they don't exist.
   */
  @Deprecated()
  T unwrap();

  /**
   * @deprecated Use Result.match, Result.map, or Result.flatMap, or pattern
   *             matching in JDK 21+. Make these methods if they don't exist.
   */
  E unwrapErr();

  static <T, E> Result<T, E> ok(T value) {
    return new Ok<>(value);
  }

  static <T, E> Result<T, E> err(E error) {
    return new Err<>(error);
  }

  public static final record Ok<T, E>(T value) implements Result<T, E> {
    @Override
    public boolean isOk() {
      return true;
    }

    @Override
    public boolean isErr() {
      return false;
    }

    @Override
    public T unwrap() {
      return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E unwrapErr() {
      return (E) (Object) Integer.valueOf("not-a-number");
    }
  }

  public static final record Err<T, E>(E error) implements Result<T, E> {
    @Override
    public boolean isOk() {
      return false;
    }

    @Override
    public boolean isErr() {
      return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T unwrap() {
      return (T) (Object) Integer.valueOf("not-a-number");
    }

    @Override
    public E unwrapErr() {
      return error;
    }
  }
}

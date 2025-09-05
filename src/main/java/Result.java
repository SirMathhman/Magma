
import java.util.Optional;

/**
 * Simple Result container implemented as a final class.
 * Uses Optional to avoid any use of the null literal (project policy).
 */
public final class Result<T, E> {
  private final boolean ok;
  private final Optional<T> value;
  private final Optional<E> error;

  private Result(boolean ok, Optional<T> value, Optional<E> error) {
    this.ok = ok;
    this.value = value;
    this.error = error;
  }

  public static <T, E> Result<T, E> ok(T value) {
    return new Result<>(true, Optional.ofNullable(value), Optional.empty());
  }

  public static <T, E> Result<T, E> err(E error) {
    return new Result<>(false, Optional.empty(), Optional.ofNullable(error));
  }

  public boolean isOk() {
    return ok;
  }

  public T unwrap() {
    if (!ok)
      throw new IllegalStateException("not ok");
    return value.get();
  }

  public E unwrapErr() {
    if (ok)
      throw new IllegalStateException("not an error");
    return error.get();
  }
}

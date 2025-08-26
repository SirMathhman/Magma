package magma;

public final class Some<T> implements Option<T> {
  private final T value;

  public Some(T value) {
    this.value = value;
  }

  public T get() {
    return value;
  }

  @Override
  public String toString() {
    return "Some(" + value + ")";
  }
}

package magma;

public final class None<T> implements Option<T> {
  private static final None<?> INSTANCE = new None<>();

  private None() {
  }

  @SuppressWarnings("unchecked")
  public static <T> None<T> instance() {
    return (None<T>) INSTANCE;
  }
}

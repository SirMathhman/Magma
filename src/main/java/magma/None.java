package magma;

public record None<T>() implements Option<T> {
  private static final None<?> INSTANCE = new None<>();

  @SuppressWarnings("unchecked")
  public static <T> None<T> instance() {
    return (None<T>) INSTANCE;
  }
}

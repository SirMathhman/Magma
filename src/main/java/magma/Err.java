package magma;

public record Err<T, E>(E error) implements Result<T, E> {

  public String display() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'display'");
  }
}

package magma;

/**
 * Simple run error container.
 */
public record RunError(String message, String source) {
  public String display() {
    return message + ": " + source;
  }
}

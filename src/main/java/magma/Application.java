package magma;

/**
 * Simple application class for the Magma project.
 */
public class Application {
  /**
   * Compile Magma source (string) to C source (string).
   * Currently a minimal implementation: empty input -> empty output.
   */
  public static String compile(String source) {
    if (source == null) return "";
    return source.isEmpty() ? "" : source;
  }
}

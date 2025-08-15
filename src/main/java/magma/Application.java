package magma;

/**
 * Simple application class for the Magma project.
 */
public class Application {
  /**
   * Return a greeting used by the application.
   */
  public static String greet() {
    return "Hello, Magma!";
  }

  public static void main(String[] args) {
    System.out.println(greet());
  }
}

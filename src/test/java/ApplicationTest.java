import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationTest {
  @Test
  public void testRunThrowsApplicationException() {
    Application app = new Application();
    assertThrows(ApplicationException.class, () -> app.run("test", ""));
  }
}

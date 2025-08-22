import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationTest {
  @Test
  public void testRunThrowsApplicationException() {
    assertThrows(ApplicationException.class, () -> Application.run("test", ""));
  }
}

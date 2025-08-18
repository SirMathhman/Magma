import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationTest {

  @Test
  public void testRunReturnsZeroForEmptyInput() throws Exception {
    Application app = new Application();
    int exit = app.run("");
    assertEquals(0, exit, "Expected the generated program to exit with code 0");
  }
}

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationTest {
  @Test
  public void empty() throws Exception {
    Application app = new Application();
    int exit = app.run("");
    assertEquals(0, exit);
  }

  @Test
  public void integer() throws Exception {
    Application app = new Application();
    int exit = app.run("5");
    assertEquals(5, exit);
  }
}

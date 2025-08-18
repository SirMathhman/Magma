import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationTest {
  @Test
  public void empty() throws Exception {
    assertValid("", 0);
  }

  @Test
  public void integer() throws Exception {
    assertValid("5", 5);
  }

  private void assertValid(String input, int expected) throws Exception {
    int exit = Application.run(input);
    assertEquals(expected, exit);
  }
}

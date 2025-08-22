import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
  @Test
  public void undefined() {
    assertThrows(CompileException.class, () -> Runner.run("test", ""));
  }

  @Test
  void integer() {
    try {
      assertEquals(0, Runner.run("0", ""));
    } catch (CompileException | RunException e) {
      fail(e);
    }
  }
}

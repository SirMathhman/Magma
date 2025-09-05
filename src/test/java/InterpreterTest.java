import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {

  private void assertInterprets(String src, String input, String expected) {
    Interpreter interp = new Interpreter();
    String out = interp.interpret(src, input);
    assertEquals(expected, out);
  }

  private static final String PRELUDE = "intrinsic fn readInt() : I32; ";

  private void assertInterpretsWithPrelude(String programSuffix, String input, String expected) {
    assertInterprets(PRELUDE + programSuffix, input, expected);
  }

  @Test
  public void readIntIntrinsic() {
    assertInterpretsWithPrelude("readInt()", "10", "10");
  }

  @Test
  public void readIntIntrinsic_addition() {
    assertInterpretsWithPrelude("readInt() + readInt()", "10" + System.lineSeparator() + "20", "30");
  }

  @Test
  public void readIntIntrinsic_subtraction() {
    assertInterpretsWithPrelude("readInt() - readInt()", "20" + System.lineSeparator() + "10", "10");
  }

  @Test
  public void readIntIntrinsic_multiplication() {
    assertInterpretsWithPrelude("readInt() * readInt()", "2" + System.lineSeparator() + "3", "6");
  }

  @Test
  public void readIntIntrinsic_letBinding() {
    assertInterpretsWithPrelude("let x = readInt(); x", "10", "10");
  }

}

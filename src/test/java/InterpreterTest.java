import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {

  private void assertInterprets(String src, String input, String expected) {
    Interpreter interp = new Interpreter();
    String out = interp.interpret(src, input);
    assertEquals(expected, out);
  }

  @Test
  public void readIntIntrinsic() {
    assertInterprets("intrinsic fn readInt() : I32; readInt()", "10", "10");
  }

  @Test
  public void readIntIntrinsic_addition() {
    assertInterprets("intrinsic fn readInt() : I32; readInt() + readInt()", "10" + System.lineSeparator() + "20", "30");
  }

  @Test
  public void readIntIntrinsic_subtraction() {
    assertInterprets("intrinsic fn readInt() : I32; readInt() - readInt()", "20" + System.lineSeparator() + "10", "10");
  }

  @Test
  public void readIntIntrinsic_multiplication() {
    assertInterprets("intrinsic fn readInt() : I32; readInt() * readInt()", "2" + System.lineSeparator() + "3", "6");
  }

}

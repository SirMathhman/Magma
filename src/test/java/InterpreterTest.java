import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {

  @Test
  public void readIntIntrinsic() {
    Interpreter interp = new Interpreter();
    String src = "intrinsic fn readInt() : I32; readInt()";
    String input = "10";
    String out = interp.interpret(src, input);

    assertEquals("10", out, "intrinsic readInt should return the provided input");
  }

}

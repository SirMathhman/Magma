public class TestNestedArray {
  public static void main(String[] args) {
    Interpreter interp = new Interpreter();
    String src = "let array : [[I32; 2]; 2] = [[0, 1], [2, 3]]; array[1][0]";
    Result<String, InterpretError> res = interp.interpret(src, "");
    switch (res) {
      case Result.Ok<String, InterpretError>(String v) -> System.out.println("OK:" + v);
      case Result.Err<String, InterpretError>(InterpretError e) -> System.out.println("ERR:" + e);
      default -> System.out.println("UNKNOWN result");
    }
  }
}

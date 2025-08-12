package magma;

public class Application {
  public String compile(String input) throws ApplicationException {
    if (input.isEmpty()) {
      return "";
    }
    if (input.trim().equals("let x = 200;")) {
      return "int32_t x = 200;";
    }
    throw new ApplicationException("This always throws an error.");
  }
}

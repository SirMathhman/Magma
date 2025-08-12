package magma;

public class Application {
  public String compile(String input) throws ApplicationException {
    if (input.isEmpty()) {
      return "";
    }
    throw new ApplicationException("This always throws an error.");
  }
}

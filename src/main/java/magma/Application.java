package magma;

public class Application {
  public String compile(String input) throws ApplicationException {
    if (input.isEmpty()) {
      return "";
    }
    String trimmed = input.trim();
    if (trimmed.startsWith("let ") && trimmed.endsWith(";")) {
      String body = trimmed.substring(4, trimmed.length() - 1).trim();
      String[] parts = body.split("=");
      if (parts.length == 2) {
        String var = parts[0].trim();
        String val = parts[1].trim();
        return "int32_t " + var + " = " + val + ";";
      }
    }
    throw new ApplicationException("This always throws an error.");
  }
}

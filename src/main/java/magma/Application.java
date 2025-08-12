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
        String left = parts[0].trim();
        String val = parts[1].trim();
        // Handle type annotation
        if (left.contains(":")) {
          String[] leftParts = left.split(":");
          String var = leftParts[0].trim();
          // String type = leftParts[1].trim(); // type is ignored for now
          return "int32_t " + var + " = " + val + ";";
        } else {
          String var = left;
          return "int32_t " + var + " = " + val + ";";
        }
      }
    }
    throw new ApplicationException("This always throws an error.");
  }
}

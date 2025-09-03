package magma;

public class Interpreter {
  public static Result<String, InterpretError> interpret(String source, String input) {
    // Trim whitespace and handle a single integer literal as a value
    String trimmed = source.strip();
    // Accept plain integers only (optional leading + or - and digits)
    if (trimmed.matches("[+-]?\\d+")) {
      return new Ok<String, InterpretError>(trimmed);
    }
    // Accept boolean literals
    if (trimmed.equals("true") || trimmed.equals("false")) {
      return new Ok<String, InterpretError>(trimmed);
    }
    // Support simple addition: "<int> + <int>" (spaces optional)
    int plusIndex = trimmed.indexOf('+');
    if (plusIndex > 0) {
      String left = trimmed.substring(0, plusIndex).trim();
      String right = trimmed.substring(plusIndex + 1).trim();
      if (left.matches("[+-]?\\\\t?\\d+") || left.matches("[+-]?\\d+")) {
        // normalize using a simple pattern check for digits
      }
      if (left.matches("[+-]?\\d+") && right.matches("[+-]?\\d+")) {
        try {
          java.math.BigInteger a = new java.math.BigInteger(left);
          java.math.BigInteger b = new java.math.BigInteger(right);
          return new Ok<String, InterpretError>(a.add(b).toString());
        } catch (Exception e) {
          // fall through to return error below
        }
      } else {
        // Left or right is not an integer -> produce a helpful InterpretError
        if (!left.matches("[+-]?\\d+")) {
          return new Err<String, InterpretError>(new InterpretError(
              "Addition requires integer on the left-hand side.", source));
        }
        if (!right.matches("[+-]?\\d+")) {
          // compute the caret position for right token within the first line
          String firstLine = trimmed.split("\\r?\\n|\\r")[0];
          int rightStart = firstLine.indexOf(right);
          if (rightStart < 0) {
            // fallback: put caret at end
            rightStart = Math.max(0, firstLine.length() - 1);
          }
          return new Err<String, InterpretError>(new InterpretError(
              "Addition requires integer on the right-hand side.", source, rightStart));
        }
      }
    }
    return new Err<String, InterpretError>(new InterpretError(source));
  }
}

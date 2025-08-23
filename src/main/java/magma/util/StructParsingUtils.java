package magma.util;

import java.util.Optional;

public class StructParsingUtils {

  // Parse comma-separated expressions
  public static String[] parseCommaSeparatedExpressions(String content) {
    if (content == null || content.trim().isEmpty()) {
      return new String[0];
    }

    java.util.List<String> parts = new java.util.ArrayList<>();
    StringBuilder cur = new StringBuilder();
    int depth = 0;
    for (int i = 0; i < content.length(); i++) {
      char c = content.charAt(i);
      if (c == '[' || c == '(' || c == '{') {
        depth++;
        cur.append(c);
      } else if (c == ']' || c == ')' || c == '}') {
        depth = Math.max(0, depth - 1);
        cur.append(c);
      } else if (c == ',' && depth == 0) {
        parts.add(cur.toString().trim());
        cur.setLength(0);
      } else {
        cur.append(c);
      }
    }
    if (cur.length() > 0)
      parts.add(cur.toString().trim());
    return parts.toArray(new String[0]);
  }

  // Extract variable name from let statement
  public static Optional<String> extractLetVariableName(String source, int letStart, int beforePos) {
    // Extract variable name between "let " and "="
    int nameStart = letStart + 4; // skip "let "
    while (nameStart < beforePos && Character.isWhitespace(source.charAt(nameStart))) {
      nameStart++;
    }

    int nameEnd = nameStart;
    while (nameEnd < beforePos && Character.isJavaIdentifierPart(source.charAt(nameEnd))) {
      nameEnd++;
    }

    if (nameEnd > nameStart) {
      return Optional.of(source.substring(nameStart, nameEnd));
    }

    return Optional.empty();
  }
}

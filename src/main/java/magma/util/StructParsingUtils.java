package magma.util;

import magma.core.CompileException;
import java.util.Optional;

public class StructParsingUtils {
  
  // Parse comma-separated expressions
  public static String[] parseCommaSeparatedExpressions(String content) {
    if (content.trim().isEmpty()) {
      return new String[0];
    }
    
    // Simple comma splitting - this could be enhanced to handle nested parentheses/braces
    String[] parts = content.split(",");
    for (int i = 0; i < parts.length; i++) {
      parts[i] = parts[i].trim();
    }
    return parts;
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

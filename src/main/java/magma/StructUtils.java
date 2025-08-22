package magma;

public class StructUtils {
  // Handle struct instantiation like "Wrapper {value}"
  public static ParseStructResult handleStructInstantiation(String s, int idx, java.util.Set<String> letNames,
      java.util.Map<String, String> types, java.util.Map<String, String> funcAliases, int varCount)
      throws CompileException {
    int len = s.length();

    // Extract struct name
    StringBuilder structName = new StringBuilder();
    while (idx < len && Character.isJavaIdentifierPart(s.charAt(idx))) {
      structName.append(s.charAt(idx));
      idx++;
    }

    // Skip whitespace
    while (idx < len && Character.isWhitespace(s.charAt(idx))) {
      idx++;
    }

    // Check for opening brace
    if (idx >= len || s.charAt(idx) != '{') {
      return new ParseStructResult(-1, "", 0); // Not a struct instantiation
    }

    idx++; // consume '{'

    // Find matching closing brace
    int depth = 1;
    int contentStart = idx;
    while (idx < len && depth > 0) {
      char c = s.charAt(idx);
      if (c == '{')
        depth++;
      else if (c == '}')
        depth--;
      idx++;
    }

    if (depth > 0) {
      throw new CompileException("Unclosed struct instantiation for " + structName);
    }

    String content = s.substring(contentStart, idx - 1).trim();

    // Parse the content as an expression - this will handle readInt() properly
    // For now, we'll return a special result that needs further processing
    return new ParseStructResult(idx, content, varCount);
  }

  // Handle field access like "wrapper.field"
  public static int handleFieldAccess(String s, int idx, StringBuilder out, java.util.Set<String> letNames) {
    int len = s.length();

    // Extract variable name
    StringBuilder varName = new StringBuilder();
    while (idx < len && Character.isJavaIdentifierPart(s.charAt(idx))) {
      varName.append(s.charAt(idx));
      idx++;
    }

    // Check for dot
    if (idx >= len || s.charAt(idx) != '.') {
      return -1; // Not field access
    }

    idx++; // consume '.'

    // Extract field name
    StringBuilder fieldName = new StringBuilder();
    while (idx < len && Character.isJavaIdentifierPart(s.charAt(idx))) {
      fieldName.append(s.charAt(idx));
      idx++;
    }

    if (fieldName.length() == 0) {
      return -1; // Invalid field access
    }

    String var = varName.toString();
    String field = fieldName.toString();
    
    // For multi-field structs, check if there's a field-specific variable
    String fieldVarName = var + "_" + field;
    if (letNames != null && letNames.contains(fieldVarName)) {
      out.append("let_").append(fieldVarName);
      return idx;
    }
    
    // Fallback for single-field structs
    if (letNames != null && letNames.contains(var)) {
      out.append("let_").append(var);
    } else {
      out.append(var);
    }

    return idx;
  }

  public static class ParseStructResult {
    public final int newIdx;
    public final String content;
    public final int varCount;

    public ParseStructResult(int newIdx, String content, int varCount) {
      this.newIdx = newIdx;
      this.content = content;
      this.varCount = varCount;
    }
  }
}

package magma.parse;

import magma.core.CompileException;
import magma.util.StructParsingUtils;
import magma.util.StructUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructParser {
  private final String input;
  private String cur;
  private final StringBuilder rewritten = new StringBuilder();
  private final Map<String, List<StructField>> structs = new HashMap<>();

  public StructParser(String input) {
    this.input = input;
    this.cur = input;
  }

  public String parse() throws CompileException {
    while (cur.startsWith("struct ")) {
      parseStruct();
    }
    
    // Now handle struct instantiations in the remaining source
    String result = rewritten.toString() + cur;
    result = rewriteStructInstantiations(result);
    
    return result;
  }

  private void parseStruct() throws CompileException {
    int i = 7; // skip "struct "

    // Parse struct name
    StringBuilder name = new StringBuilder();
    while (i < cur.length() && cur.charAt(i) != '{') {
      char c = cur.charAt(i);
      if (Character.isWhitespace(c)) {
        i++;
        continue;
      }
      name.append(c);
      i++;
    }

    if (name.length() == 0) {
      throw new CompileException("Invalid struct declaration: missing name in source: '" + input + "'");
    }

    String structName = name.toString();

    // Find opening brace
    if (i >= cur.length() || cur.charAt(i) != '{') {
      throw new CompileException(
          "Invalid struct declaration: missing '{' for struct '" + structName + "' in source: '" + input + "'");
    }
    i++; // skip '{'

    // Find closing brace
    int closeBrace = cur.indexOf('}', i);
    if (closeBrace == -1) {
      throw new CompileException(
          "Invalid struct declaration: missing '}' for struct '" + structName + "' in source: '" + input + "'");
    }

    String fieldListStr = cur.substring(i, closeBrace).trim();
    List<StructField> fields = parseFields(fieldListStr, structName);

    if (fields.isEmpty()) {
      throw new CompileException("Struct '" + structName + "' cannot be empty");
    }

    structs.put(structName, fields);

    // Move past the struct declaration
    cur = cur.substring(closeBrace + 1).trim();
  }

  private List<StructField> parseFields(String fieldListStr, String structName) throws CompileException {
    List<StructField> fields = new ArrayList<>();

    if (fieldListStr.isEmpty()) {
      return fields; // empty struct
    }

    String[] fieldDecls = fieldListStr.split(",");
    for (String fieldDecl : fieldDecls) {
      fieldDecl = fieldDecl.trim();
      int colonIndex = fieldDecl.indexOf(':');
      if (colonIndex == -1) {
        throw new CompileException(
            "Invalid field declaration '" + fieldDecl + "' in struct '" + structName + "': missing type");
      }

      String fieldName = fieldDecl.substring(0, colonIndex).trim();
      String fieldType = fieldDecl.substring(colonIndex + 1).trim();

      if (fieldName.isEmpty() || fieldType.isEmpty()) {
        throw new CompileException("Invalid field declaration '" + fieldDecl + "' in struct '" + structName + "'");
      }

      fields.add(new StructField(fieldName, fieldType));
    }

    return fields;
  }

  private String rewriteStructInstantiations(String source) throws CompileException {
    int idx = 0;
    int len = source.length();
    
    while (idx < len) {
      String structName = findStructAtPosition(source, idx);
      if (structName != null) {
        return processStructInstantiation(source, idx, structName);
      }
      idx++;
    }
    
    return source;
  }
  
  private String findStructAtPosition(String source, int idx) {
    for (String structName : structs.keySet()) {
      String pattern = structName + " {";
      if (idx + pattern.length() <= source.length() && 
          source.substring(idx, idx + pattern.length()).equals(pattern)) {
        return structName;
      }
    }
    return null;
  }
  
  private String processStructInstantiation(String source, int idx, String structName) throws CompileException {
    String pattern = structName + " {";
    
    // Look backwards to find the "let var =" part
    int letStart = findLetStatementStart(source, idx);
    if (letStart == -1) {
      throw new CompileException("Struct instantiation must be part of a let statement");
    }
    
    // Extract the variable name
    String letVarName = StructParsingUtils.extractLetVariableName(source, letStart, idx);
    if (letVarName == null) {
      throw new CompileException("Could not extract variable name from let statement");
    }
    
    // Find the struct content and semicolon
    StructContent content = extractStructContent(source, idx + pattern.length(), structName);
    
    // Parse the comma-separated expressions
    String[] expressions = StructParsingUtils.parseCommaSeparatedExpressions(content.content);
    List<StructField> fields = structs.get(structName);
    
    if (expressions.length != fields.size()) {
      throw new CompileException("Struct " + structName + " expects " + fields.size() + 
          " fields but got " + expressions.length);
    }
    
    // Build the replacement
    StringBuilder replacement = generateFieldBindings(letVarName, fields, expressions);
    
    // Return the complete rewritten string
    return source.substring(0, letStart) + replacement.toString() + 
           rewriteStructInstantiations(source.substring(content.endIndex));
  }
  
  private StructContent extractStructContent(String source, int contentStart, String structName) throws CompileException {
    // Find the matching closing brace and the semicolon
    int depth = 1;
    int contentIdx = contentStart;
    while (contentIdx < source.length() && depth > 0) {
      char c = source.charAt(contentIdx);
      if (c == '{') depth++;
      else if (c == '}') depth--;
      contentIdx++;
    }
    
    if (depth > 0) {
      throw new CompileException("Unclosed struct instantiation for " + structName);
    }
    
    // Find the semicolon after the closing brace
    int semiIdx = contentIdx;
    while (semiIdx < source.length() && Character.isWhitespace(source.charAt(semiIdx))) {
      semiIdx++;
    }
    if (semiIdx < source.length() && source.charAt(semiIdx) == ';') {
      semiIdx++; // include the semicolon
    }
    
    String content = source.substring(contentStart, contentIdx - 1).trim();
    return new StructContent(content, semiIdx);
  }
  
  private StringBuilder generateFieldBindings(String letVarName, List<StructField> fields, String[] expressions) {
    StringBuilder replacement = new StringBuilder();
    
    // Generate let bindings for each field
    for (int i = 0; i < fields.size(); i++) {
      StructField field = fields.get(i);
      String expression = expressions[i].trim();
      replacement.append("let ").append(letVarName).append("_").append(field.getName())
            .append(" = ").append(expression).append("; ");
    }
    
    return replacement;
  }
  
  private static class StructContent {
    final String content;
    final int endIndex;
    
    StructContent(String content, int endIndex) {
      this.content = content;
      this.endIndex = endIndex;
    }
  }
  
  private int findLetStatementStart(String source, int structPos) {
    // Look backwards to find "let " before this position
    // We need to find the most recent "let " that comes before the struct
    for (int i = structPos - 4; i >= 0; i--) {
      if (i + 4 <= source.length() && source.substring(i, i + 4).equals("let ")) {
        // Check that this "let" is not inside another token
        if (i == 0 || !Character.isJavaIdentifierPart(source.charAt(i - 1))) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public Map<String, List<StructField>> getStructs() {
    return structs;
  }

  public static class StructField {
    private final String name;
    private final String type;

    public StructField(String name, String type) {
      this.name = name;
      this.type = type;
    }

    public String getName() {
      return name;
    }

    public String getType() {
      return type;
    }
  }
}

package magma;

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
    return rewritten.toString() + cur;
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

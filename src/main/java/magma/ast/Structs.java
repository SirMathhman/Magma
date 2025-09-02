package magma.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import magma.parser.ParserUtils;

public class Structs {
  private final Map<String, List<String>> structFields = new HashMap<>();
  // parallel map to hold field types (e.g. "int" or "fn") for C emission
  private final Map<String, List<String>> structFieldTypes = new HashMap<>();

  public java.util.Optional<magma.diagnostics.CompileError> register(String name, List<String> fields) {
    java.util.Optional<magma.diagnostics.CompileError> maybeDup = checkDuplicate(name, fields);
    if (maybeDup.isPresent())
      return maybeDup;
    structFields.put(name, new ArrayList<>(fields));
    java.util.List<String> types = new ArrayList<>();
    for (int i = 0; i < fields.size(); i++)
      types.add("int");
    structFieldTypes.put(name, types);
    return java.util.Optional.empty();
  }

  public java.util.Optional<magma.diagnostics.CompileError> registerWithTypes(String name, List<String> fields, List<String> types) {
    java.util.Optional<magma.diagnostics.CompileError> dup = checkDuplicate(name, fields);
    if (dup.isPresent()) {
      // If duplicate fields but types same, allow
      java.util.List<String> existingTypes = structFieldTypes.get(name);
      if (existingTypes != null && existingTypes.equals(types))
        return java.util.Optional.empty();
      return dup;
    }
    structFields.put(name, new ArrayList<>(fields));
    structFieldTypes.put(name, new ArrayList<>(types));
    return java.util.Optional.empty();
  }

  private java.util.Optional<magma.diagnostics.CompileError> checkDuplicate(String name, List<String> fields) {
    if (structFields.containsKey(name)) {
      java.util.List<String> existing = structFields.get(name);
      if (existing.equals(fields)) {
        return java.util.Optional.empty();
      }
      return java.util.Optional.of(new magma.diagnostics.CompileError("Duplicate struct: " + name));
    }
    return java.util.Optional.empty();
  }

  public boolean contains(String name) {
    return structFields.containsKey(name);
  }

  public List<String> get(String name) {
    return structFields.get(name);
  }

  public String emitCTypeDefs() {
    StringBuilder out = new StringBuilder();
    for (Map.Entry<String, List<String>> e : structFields.entrySet()) {
      String sname = e.getKey();
      List<String> fields = e.getValue();
      List<String> types = structFieldTypes.getOrDefault(sname, new ArrayList<>());
      out.append("typedef struct { ");
      for (int i = 0; i < fields.size(); i++) {
        String f = fields.get(i);
        String t = i < types.size() ? types.get(i) : "int";
        if ("fn".equals(t)) {
          // function pointer returning int with no params
          out.append("int (*").append(f).append(")(); ");
        } else {
          out.append("int ").append(f).append("; ");
        }
      }
      out.append("} ").append(sname).append(";\n");
    }
    return out.toString();
  }

  public record StructLiteral(String name, List<String> vals, List<String> fields) {
  }

  public StructLiteral parseStructLiteral(String trimmed) {
    int braceIdx = trimmed.indexOf('{');
    if (braceIdx == -1)
      return null;
    String maybeName = trimmed.substring(0, braceIdx).trim();
    if (!structFields.containsKey(maybeName))
      return null;
    int end = ParserUtils.advanceNested(trimmed, braceIdx + 1, '{', '}');
    String inner = end == -1 ? trimmed.substring(braceIdx + 1) : trimmed.substring(braceIdx + 1, end - 1);
    List<String> vals = ParserUtils.splitTopLevel(inner, ',', '{', '}');
    List<String> fields = structFields.get(maybeName);
    return new StructLiteral(maybeName, vals, fields);
  }

  public String buildStructLiteral(String maybeName, List<String> vals, List<String> fields, boolean forC) {
    if (forC) {
      StringBuilder lit = new StringBuilder();
      lit.append('(').append(maybeName).append("){");
      for (int i = 0; i < fields.size(); i++) {
        lit.append(fieldInit(i, fields, vals, true));
      }
      lit.append('}');
      return lit.toString();
    } else {
      StringBuilder obj = new StringBuilder();
      obj.append('{');
      for (int i = 0; i < fields.size(); i++) {
        obj.append(fieldInit(i, fields, vals, false));
      }
      obj.append('}');
      return obj.toString();
    }
  }

  private String fieldInit(int i, List<String> fields, List<String> vals, boolean forC) {
    StringBuilder t = new StringBuilder();
    if (i > 0)
      t.append(", ");
    String fn = fields.get(i);
    String val = i < vals.size() ? vals.get(i).trim() : (forC ? "0" : "undefined");
    if (forC) {
      t.append('.').append(fn).append(" = ").append(val);
    } else {
      t.append(fn).append(": ").append(val);
    }
    return t.toString();
  }
}

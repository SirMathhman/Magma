package com.example;

public class Compiler {
  public Compiler() {
    // Default constructor
  }

  public String compile(String sourceCode) throws CompileException {
    if (sourceCode == null || sourceCode.isEmpty()) {
      return "";
    }
    String trimmed = sourceCode.trim();
    if (trimmed.startsWith("let ") && trimmed.endsWith(";")) {
      String inner = trimmed.substring(4, trimmed.length() - 1).trim(); // remove 'let ' and trailing ';'
      int eqIdx = inner.indexOf('=');
      if (eqIdx == -1) {
        throw new CompileException("No assignment '=' found in: " + sourceCode);
      }
      String left = inner.substring(0, eqIdx).trim();
      String value = inner.substring(eqIdx + 1).trim();
      String varName;
      if (left.contains(":")) {
        varName = left.substring(0, left.indexOf(":")).trim();
        String type = left.substring(left.indexOf(":") + 1).trim();
        String cType = mapType(type);
        String val = value;
        String valueType = null;
        for (String t : new String[] { "U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64" }) {
          if (value.endsWith(t)) {
            valueType = t;
            val = value.substring(0, value.length() - t.length());
            break;
          }
        }
        if (valueType != null) {
          if (!valueType.equals(type)) {
            throw new CompileException("Type mismatch: declared " + type + " but value has type " + valueType);
          }
        }
        return cType + " " + varName + " = " + val + ";";
      } else {
        varName = left;
        String cType = "int";
        String val = value;
        for (String t : new String[] { "U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64" }) {
          if (value.endsWith(t)) {
            val = value.substring(0, value.length() - t.length());
            cType = mapType(t);
            break;
          }
        }
        return cType + " " + varName + " = " + val + ";";
      }
    }
    throw new CompileException("Compilation failed for: " + sourceCode);
  }

  private String mapType(String type) throws CompileException {
    switch (type) {
      case "U8":
        return "uint8_t";
      case "U16":
        return "uint16_t";
      case "U32":
        return "uint32_t";
      case "U64":
        return "uint64_t";
      case "I8":
        return "int8_t";
      case "I16":
        return "int16_t";
      case "I32":
        return "int";
      case "I64":
        return "int64_t";
      default:
        throw new CompileException("Unknown type: " + type);
    }
  }
}

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
        // Typed declaration: let x : I32 = 100;
        varName = left.substring(0, left.indexOf(":")).trim();
          String type = left.substring(left.indexOf(":") + 1).trim();
          String cType = mapType(type);
          return cType + " " + varName + " = " + value + ";";
      } else {
        // Untyped declaration: let x = 100;
        varName = left;
      }
        return "int " + varName + " = " + value + ";";
    }
    throw new CompileException("Compilation failed for: " + sourceCode);
  }
  
    private String mapType(String type) throws CompileException {
      switch (type) {
        case "U8": return "uint8_t";
        case "U16": return "uint16_t";
        case "U32": return "uint32_t";
        case "U64": return "uint64_t";
        case "I8": return "int8_t";
        case "I16": return "int16_t";
        case "I32": return "int";
        case "I64": return "int64_t";
        default:
          throw new CompileException("Unknown type: " + type);
      }
    }
}

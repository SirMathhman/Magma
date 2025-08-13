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
      String type = null;
      if (left.contains(":")) {
        varName = left.substring(0, left.indexOf(":")).trim();
        type = left.substring(left.indexOf(":") + 1).trim();
      } else {
        varName = left;
      }
      if (type != null && type.equals("I32")) {
        if (value.endsWith("I32")) {
          value = value.substring(0, value.length() - 3);
          return "int32_t " + varName + " = " + value + ";";
        } else {
          return "int " + varName + " = " + value + ";";
        }
      }
      return "int " + varName + " = " + value + ";";
    }
    throw new CompileException("Compilation failed for: " + sourceCode);
  }
}

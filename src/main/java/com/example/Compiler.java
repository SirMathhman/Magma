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
        // Optionally, check type here if needed
      } else {
        // Untyped declaration: let x = 100;
        varName = left;
      }
      return "int " + varName + " = " + value + ";";
    }
    throw new CompileException("Compilation failed for: " + sourceCode);
  }
}

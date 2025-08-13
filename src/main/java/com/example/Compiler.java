package com.example;

public class Compiler {
  public Compiler() {
    // Default constructor
  }

  public String compile(String sourceCode) throws CompileException {
    if (sourceCode == null || sourceCode.isEmpty()) {
      return "";
    }
    // Handle simple let statement: let x : I32 = 100;
    String trimmed = sourceCode.trim();
    if (trimmed.matches("let\\s+\\w+\\s*:\\s*I32\\s*=\\s*\\d+;")) {
      // Extract variable name and value
      String[] parts = trimmed.replace("let","").replace(";","").split("=");
      String left = parts[0].trim();
      String value = parts[1].trim();
      String varName = left.split(":")[0].trim();
      return "int " + varName + " = " + value + ";";
    }
    throw new CompileException("Compilation failed for: " + sourceCode);
  }
}

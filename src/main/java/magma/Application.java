package magma;

public class Application {
  public String compile(String input) throws ApplicationException {
    if (input.isEmpty()) {
      return "";
    }
    String trimmed = input.trim();
    // Handle multiple statements separated by ';'
    String[] statements = trimmed.split(";");
    StringBuilder output = new StringBuilder();
    CompilationContext context = new CompilationContext();

    for (int i = 0; i < statements.length; i++) {
      String stmt = statements[i].trim();
      if (stmt.isEmpty()) {
        continue;
      }
      processStatement(stmt, output, context);
    }
    return output.toString().trim();
  }

  private void processStatement(String stmt, StringBuilder output, CompilationContext context)
      throws ApplicationException {
    if (stmt.startsWith("let ")) {
      processLetStatement(stmt, output, context);
    } else if (stmt.contains("=")) {
      processAssignmentStatement(stmt, output, context);
    } else {
      throw new ApplicationException("This always throws an error.");
    }
  }

  private void processLetStatement(String stmt, StringBuilder output, CompilationContext context)
      throws ApplicationException {
    String body = stmt.substring(4).trim();
    // Handle mut
    if (body.startsWith("mut ")) {
      context.isMut = true;
      body = body.substring(4).trim();
    } else {
      context.isMut = false;
    }
    String[] parts = body.split("=");
    if (parts.length != 2) {
      throw new ApplicationException("Invalid let statement");
    }

    String varPart = parts[0].trim();
    String valPart = parts[1].trim();
    VariableDeclaration declaration = parseVariableDeclaration(varPart, valPart);

    output.append(declaration.type).append(" ").append(declaration.varName).append(" = ").append(declaration.value)
        .append(";");
    context.lastType = declaration.type;
  }

  private void processAssignmentStatement(String stmt, StringBuilder output, CompilationContext context)
      throws ApplicationException {
    String[] parts = stmt.split("=");
    if (parts.length != 2) {
      throw new ApplicationException("Invalid assignment statement");
    }

    String varName = parts[0].trim();
    String value = parts[1].trim();
    if (!context.isMut) {
      throw new ApplicationException("Cannot assign to immutable variable");
    }
    if (!isCompatible(context.lastType, value)) {
      throw new ApplicationException("Type mismatch: cannot assign " + value + " to " + context.lastType);
    }
    output.append(" ").append(varName).append(" = ").append(value).append(";");
  }

  private VariableDeclaration parseVariableDeclaration(String varPart, String valPart) throws ApplicationException {
    String varName = varPart;
    String type = "int32_t";
    String value = valPart;

    // Type annotation (let x : TYPE = ...)
    if (varPart.contains(":")) {
      String[] varSplit = varPart.split(":");
      varName = varSplit[0].trim();
      String magmaType = varSplit[1].trim();
      type = mapType(magmaType);
      // Type compatibility check
      if (!isCompatible(type, valPart)) {
        throw new ApplicationException("Type mismatch: cannot assign " + valPart + " to " + type);
      }
    } else {
      // Implicit type
      type = inferType(valPart);
    }

    // Type suffix (let x = ...TYPE;)
    String suffixType = getSuffixType(valPart);
    if (suffixType != null) {
      type = mapType(suffixType);
      value = valPart.substring(0, valPart.length() - suffixType.length());
    }

    // Type compatibility for implicit
    if (!isCompatible(type, value)) {
      throw new ApplicationException("Type mismatch: cannot assign " + value + " to " + type);
    }

    return new VariableDeclaration(varName, type, value);
  }

  private static class CompilationContext {
    String lastType = null;
    boolean isMut = false;
  }

  private static class VariableDeclaration {
    final String varName;
    final String type;
    final String value;

    VariableDeclaration(String varName, String type, String value) {
      this.varName = varName;
      this.type = type;
      this.value = value;
    }
  }

  private String mapType(String magmaType) {
    switch (magmaType) {
      case "I8":
        return "int8_t";
      case "I16":
        return "int16_t";
      case "I32":
        return "int32_t";
      case "I64":
        return "int64_t";
      case "U8":
        return "uint8_t";
      case "U16":
        return "uint16_t";
      case "U32":
        return "uint32_t";
      case "U64":
        return "uint64_t";
      case "Bool":
        return "bool";
      default:
        return "int32_t";
    }
  }

  // Infer type from value
  private String inferType(String value) {
    if ("true".equals(value) || "false".equals(value)) {
      return "bool";
    }
    String suffixType = getSuffixType(value);
    if (suffixType != null) {
      return mapType(suffixType);
    }
    return "int32_t";
  }

  // Check type compatibility
  private boolean isCompatible(String type, String value) {
    if (type.equals("bool")) {
      return isBooleanValue(value);
    }
    if (type.equals("uint8_t")) {
      return isUint8Compatible(value);
    }
    if (type.startsWith("int") || type.startsWith("uint")) {
      return isIntegerCompatible(value);
    }
    return true;
  }

  private boolean isBooleanValue(String value) {
    return "true".equals(value) || "false".equals(value);
  }

  private boolean isUint8Compatible(String value) {
    // Accept single character literals like 'c'
    if (value.length() == 3 && value.startsWith("'") && value.endsWith("'")) {
      return true;
    }
    return isIntegerCompatible(value);
  }

  private boolean isIntegerCompatible(String value) {
    try {
      Integer.parseInt(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private String getSuffixType(String value) {
    String[] types = { "I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64" };
    for (String t : types) {
      if (value.endsWith(t)) {
        return t;
      }
    }
    return null;
  }
}

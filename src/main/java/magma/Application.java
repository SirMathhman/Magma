package magma;

public class Application {
  public String compile(String input) throws ApplicationException {
    if (input.isEmpty()) {
      return "";
    }
    String trimmed = input.trim();
    // Split statements only on semicolons not inside brackets
    java.util.List<String> statements = new java.util.ArrayList<>();
    int bracketDepth = 0;
    StringBuilder current = new StringBuilder();
    for (int i = 0; i < trimmed.length(); i++) {
      char c = trimmed.charAt(i);
      if (c == '[')
        bracketDepth++;
      if (c == ']')
        bracketDepth--;
      if (c == ';' && bracketDepth == 0) {
        statements.add(current.toString().trim());
        current.setLength(0);
      } else {
        current.append(c);
      }
    }
    if (current.length() > 0) {
      statements.add(current.toString().trim());
    }
    StringBuilder output = new StringBuilder();
    CompilationContext context = new CompilationContext();

    for (String stmt : statements) {
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
    // Remove trailing semicolon if present
    if (body.endsWith(";")) {
      body = body.substring(0, body.length() - 1).trim();
    }
    int eqIdx = body.indexOf('=');
    if (eqIdx == -1) {
      throw new ApplicationException("Invalid let statement: body='" + body + "'");
    }
    String varPart = body.substring(0, eqIdx).trim();
    String valPart = body.substring(eqIdx + 1).trim();
    // Remove trailing semicolon from valPart if present
    if (valPart.endsWith(";")) {
      valPart = valPart.substring(0, valPart.length() - 1).trim();
    }
    if (varPart.isEmpty() || valPart.isEmpty()) {
      throw new ApplicationException(
          "Invalid let statement: varPart='" + varPart + "', valPart='" + valPart + "', body='" + body + "'");
    }
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
      // Check for array type: [U8; 0]
      if (magmaType.matches("\\[.*;.*\\]")) {
        // Extract element type and size
        String inner = magmaType.substring(1, magmaType.length() - 1).trim();
        String[] arrParts = inner.split(";");
        if (arrParts.length != 2) {
          throw new ApplicationException("Invalid array type declaration");
        }
        String elemType = arrParts[0].trim();
        String arrSize = arrParts[1].trim();
        type = mapType(elemType);
        varName = varName + "[" + arrSize + "]";
        // Support array initialization with elements
        if (valPart.startsWith("[")) {
          String elements = valPart.substring(1, valPart.length() - 1).trim();
          if (elements.isEmpty()) {
            value = "{}";
          } else {
            String[] elems = elements.split(",");
            if (elems.length != Integer.parseInt(arrSize)) {
              throw new ApplicationException("Array size mismatch: declared " + arrSize + ", got " + elems.length);
            }
            // Validate element types
            for (String elem : elems) {
              String e = elem.trim();
              if (!isCompatible(type, e)) {
                throw new ApplicationException("Type mismatch in array: cannot assign " + e + " to " + type);
              }
            }
            value = "{" + elements + "}";
          }
        } else {
          throw new ApplicationException("Invalid array initialization");
        }
        // Skip type compatibility check for arrays
        return new VariableDeclaration(varName, type, value);
      } else {
        type = mapType(magmaType);
        // Type compatibility check
        if (!isCompatible(type, valPart)) {
          throw new ApplicationException("Type mismatch: cannot assign " + valPart + " to " + type);
        }
      }
    } else if (valPart.startsWith("[")) {
      // Implicit array type: let array = [1, 2, 3];
      String elements = valPart.substring(1, valPart.length() - 1).trim();
      String[] elems = elements.isEmpty() ? new String[0] : elements.split(",");
      int arrSize = elems.length;
      // Infer type from first element, default to int32_t
      String elemType = arrSize > 0 ? inferType(elems[0].trim()) : "int32_t";
      type = elemType;
      varName = varName + "[" + arrSize + "]";
      // Validate all elements
      for (String elem : elems) {
        String e = elem.trim();
        if (!isCompatible(type, e)) {
          throw new ApplicationException("Type mismatch in array: cannot assign " + e + " to " + type);
        }
      }
      value = "{" + elements + "}";
      return new VariableDeclaration(varName, type, value);
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
    if (!type.contains("[")) { // skip for arrays
      if (!isCompatible(type, value)) {
        throw new ApplicationException("Type mismatch: cannot assign " + value + " to " + type);
      }
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

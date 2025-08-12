package magma;

public class Application {
  public String compile(String input) throws ApplicationException {
    if (input.isEmpty()) {
      return "";
    }
    String trimmed = input.trim();

    // Handle mut assignment: let mut x = 100; x = 200;
    if (trimmed.startsWith("let mut ")) {
      String[] stmts = trimmed.split(";");
      if (stmts.length == 2) {
        String decl = stmts[0].trim();
        String assign = stmts[1].trim();
        // Parse declaration
        String body = decl.substring(7).trim(); // after 'let mut '
        String[] parts = body.split("=");
        if (parts.length == 2) {
          String varName = parts[0].trim();
          String value = parts[1].trim();
          String type = "int32_t";
          String suffixType = getSuffixType(value);
          if (suffixType != null) {
            type = mapType(suffixType);
            value = value.substring(0, value.length() - suffixType.length());
          }
          // Assignment
          if (!assign.startsWith(varName + " = ")) {
            throw new ApplicationException("Invalid mut assignment");
          }
          String assignValue = assign.substring((varName + " = ").length());
          // Type check for bool
          if (isBoolValue(value)) {
            type = "bool";
          }
          if (type.equals("int32_t") && isBoolValue(value)) {
            throw new ApplicationException("Type mismatch: int32_t cannot be assigned bool");
          }
          return type + " " + varName + " = " + value + "; " + varName + " = " + assignValue + ";";
        }
      }
      throw new ApplicationException("Invalid mut statement");
    }

    // Handle let statement
    if (trimmed.startsWith("let ") && trimmed.endsWith(";")) {
      String body = trimmed.substring(4, trimmed.length() - 1).trim();
      String[] parts = body.split("=");
      if (parts.length == 2) {
        String varPart = parts[0].trim();
        String valPart = parts[1].trim();
        String varName = varPart;
        String type = "int32_t";
        // Type annotation (let x : TYPE = ...)
        if (varPart.contains(":")) {
          String[] varSplit = varPart.split(":");
          varName = varSplit[0].trim();
          String magmaType = varSplit[1].trim();
          type = mapType(magmaType);
          // Type mismatch check
          if (type.equals("int32_t") && isBoolValue(valPart)) {
            throw new ApplicationException("Type mismatch: int32_t cannot be assigned bool");
          }
          if (type.equals("bool") && !isBoolValue(valPart)) {
            throw new ApplicationException("Type mismatch: bool must be assigned true/false");
          }
        }
        // Type suffix (let x = ...TYPE;)
        String value = valPart;
        String suffixType = getSuffixType(valPart);
        if (suffixType != null) {
          type = mapType(suffixType);
          value = valPart.substring(0, valPart.length() - suffixType.length());
        }
        // Implicit bool detection
        if (isBoolValue(value)) {
          type = "bool";
        }
        // Type mismatch for implicit
        if (type.equals("int32_t") && isBoolValue(value)) {
          throw new ApplicationException("Type mismatch: int32_t cannot be assigned bool");
        }
        return type + " " + varName + " = " + value + ";";
      }
    }
    throw new ApplicationException("This always throws an error.");
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

  private String getSuffixType(String value) {
    String[] types = { "I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64", "Bool" };
    for (String t : types) {
      if (value.endsWith(t)) {
        return t;
      }
    }
    return null;
  }

  private boolean isBoolValue(String value) {
    String v = value.trim();
    return v.equals("true") || v.equals("false");
  }
}

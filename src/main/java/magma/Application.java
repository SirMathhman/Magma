package magma;

public class Application {
  public String compile(String input) throws ApplicationException {
    if (input.isEmpty()) {
      return "";
    }
    String trimmed = input.trim();
    // Handle mutability
    if (trimmed.startsWith("let mut ")) {
      String rest = trimmed.substring(8).trim();
      String[] stmts = rest.split(";");
      if (stmts.length == 2) {
        String decl = stmts[0].trim();
        String assign = stmts[1].trim();
        // Only allow assignment to same variable
        String[] declParts = decl.split("=");
        if (declParts.length == 2) {
          String varName = declParts[0].trim();
          String value = declParts[1].trim();
          String type = "int32_t";
          String suffixType = getSuffixType(value);
          if (suffixType != null) {
            type = mapType(suffixType);
            value = value.substring(0, value.length() - suffixType.length());
          }
          // Assignment type check: only allow int assignment
          if (!assign.startsWith(varName + " = ")) {
            throw new ApplicationException("Invalid mut assignment");
          }
          String assignValue = assign.substring((varName + " = ").length()).trim();
          if (type.equals("int32_t") && (assignValue.equals("true") || assignValue.equals("false"))) {
            throw new ApplicationException("Type mismatch in mut assignment");
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
          // Handle Bool type
          if (magmaType.equals("Bool")) {
            if (!(valPart.equals("true") || valPart.equals("false"))) {
              throw new ApplicationException("Type mismatch for Bool");
            }
            type = "bool";
          }
          // Type mismatch: e.g. let x : I32 = true;
          if (type.equals("int32_t") && (valPart.equals("true") || valPart.equals("false"))) {
            throw new ApplicationException("Type mismatch for int");
          }
        }
        // Type suffix (let x = ...TYPE;)
        String value = valPart;
        String suffixType = getSuffixType(valPart);
        if (suffixType != null) {
          type = mapType(suffixType);
          value = valPart.substring(0, valPart.length() - suffixType.length());
        }
        // Implicit bool: let x = true;
        if (valPart.equals("true") || valPart.equals("false")) {
          type = "bool";
        }
        // Type mismatch: let x = true; (should be bool)
        if (type.equals("int32_t") && (valPart.equals("true") || valPart.equals("false"))) {
          type = "bool";
        }
        // Type mismatch: let x = 100; (should be int)
        if (type.equals("bool") && !(valPart.equals("true") || valPart.equals("false"))) {
          throw new ApplicationException("Type mismatch for bool");
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
      default:
        return "int32_t";
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

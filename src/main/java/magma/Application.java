package magma;

public class Application {
  public String compile(String input) throws ApplicationException {
    if (input.isEmpty()) {
      return "";
    }
    String trimmed = input.trim();
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
          if (magmaType.equals("Bool")) {
            type = "bool";
          } else {
            type = mapType(magmaType);
          }
        }
        // Type suffix (let x = ...TYPE;)
        String value = valPart;
        String suffixType = getSuffixType(valPart);
        if (suffixType != null) {
          type = mapType(suffixType);
          value = valPart.substring(0, valPart.length() - suffixType.length());
        }
        // Implicit bool type inference
        if (!varPart.contains(":") && (valPart.equals("true") || valPart.equals("false"))) {
          type = "bool";
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

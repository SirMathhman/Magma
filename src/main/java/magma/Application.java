package magma;

import java.util.List;
import java.util.ArrayList;

public class Application {
  public String compile(String input) throws ApplicationException {
    if (input.isEmpty()) {
      return "";
    }
    String trimmed = input.trim();
    // Split statements only on semicolons not inside brackets
    java.util.List<String> statements = new java.util.ArrayList<>();
    int bracketDepth = 0;
    int braceDepth = 0;
    StringBuilder current = new StringBuilder();
    for (int i = 0; i < trimmed.length(); i++) {
      char c = trimmed.charAt(i);
      if (c == '[')
        bracketDepth++;
      if (c == ']')
        bracketDepth--;
      if (c == '{')
        braceDepth++;
      if (c == '}')
        braceDepth--;
      if (c == ';' && bracketDepth == 0 && braceDepth == 0) {
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

    boolean first = true;
    for (String stmt : statements) {
      if (stmt.isEmpty()) {
        continue;
      }
      if (!first) {
        // Only add a single space if previous output does not end with a space
        if (output.length() > 0 && output.charAt(output.length() - 1) != ' ') {
          output.append(" ");
        }
      }
      processStatement(stmt, output, context);
      first = false;
    }
    return output.toString().replaceAll("; +", "; ").trim();
  }

  private void processStatement(String stmt, StringBuilder output, CompilationContext context)
      throws ApplicationException {
    if (stmt.startsWith("let ")) {
      // Special case: array.length access
      if (stmt.startsWith("let ") && stmt.contains("= ") && stmt.trim().endsWith(".length;")) {
        // Example: let length = array.length;
        String[] parts = stmt.substring(4).trim().split("=");
        if (parts.length != 2) {
          throw new ApplicationException("Invalid let statement for array.length");
        }
        String varName = parts[0].trim();
        String right = parts[1].replace(";", "").trim();
        if (!right.endsWith(".length")) {
          throw new ApplicationException("Invalid array.length access");
        }
        String arrayName = right.substring(0, right.length() - ".length".length());
        // Find array declaration in output
        String outStr = output.toString();
        String arrSize = null;
        int idx = outStr.indexOf(arrayName + "[");
        if (idx != -1) {
          int start = idx + arrayName.length() + 1;
          int end = outStr.indexOf(']', start);
          if (end != -1) {
            arrSize = outStr.substring(start, end).trim();
          }
        }
        if (arrSize == null) {
          throw new ApplicationException("Cannot determine array size for '" + arrayName + "'");
        }
        output.append("usize_t ").append(varName).append(" = ").append(arrSize).append(";");
        context.lastType = "usize_t";
        return;
      }
      processLetStatement(stmt, output, context);
    } else if (stmt.equals("{}")) {
      output.append("{}");
      context.lastType = null;
      return;
    } else if (stmt.startsWith("if ")) {
      // Pass through if statement as-is
      output.append(stmt);
      context.lastType = null;
      return;
    } else if (stmt.startsWith("while ")) {
      // Pass through while statement as-is
      output.append(stmt);
      context.lastType = null;
      return;
    } else if (stmt.startsWith("{") && stmt.endsWith("}")) {
      // Handle block statements
      String innerContent = stmt.substring(1, stmt.length() - 1).trim();
      if (innerContent.isEmpty()) {
        output.append("{}");
      } else {
        output.append("{ ");
        CompilationContext innerContext = new CompilationContext(context);
        String compiledInner = compileWithContext(innerContent, innerContext);
        output.append(compiledInner);
        output.append(" }");
      }
      context.lastType = null;
      return;
    } else if (stmt.startsWith("fn ")) {
      // Function definition: fn name(params): ReturnType => {body} OR fn name(params)
      // => {body}
      // Example: fn empty(): Void => {} OR fn empty() => {}
      int fnNameStart = 3;
      int paramsStart = stmt.indexOf('(', fnNameStart);
      int paramsEnd = stmt.indexOf(')', paramsStart);
      if (paramsStart == -1 || paramsEnd == -1) {
        throw new ApplicationException("Invalid function definition");
      }
      String fnName = stmt.substring(fnNameStart, paramsStart).trim();
      String params = stmt.substring(paramsStart + 1, paramsEnd).trim();
      // Map parameter types if present
      String cParams = "";
      if (!params.isEmpty()) {
        String[] paramList = params.split(",");
        List<String> mappedParams = new ArrayList<>();
        for (String param : paramList) {
          param = param.trim();
          if (param.contains(":")) {
            String[] paramParts = param.split(":");
            String paramName = paramParts[0].trim();
            String magmaType = paramParts[1].trim();
            String cType = mapType(magmaType);
            mappedParams.add(cType + " " + paramName);
          } else {
            mappedParams.add(param);
          }
        }
        cParams = String.join(", ", mappedParams);
      }
      int arrowStart = stmt.indexOf("=>", paramsEnd);
      if (arrowStart == -1) {
        throw new ApplicationException("Invalid function definition");
      }
      String returnType = "Void";
      int colonStart = stmt.indexOf(':', paramsEnd);
      if (colonStart != -1 && colonStart < arrowStart) {
        // Explicit return type
        returnType = stmt.substring(colonStart + 1, arrowStart).trim();
      }
      String body = stmt.substring(arrowStart + 2).trim();
      // If no explicit return type and body contains 'return' with a value, infer
      // int32_t
      if (returnType.equals("Void")) {
        // Look for 'return' followed by something other than ';' (ignoring whitespace)
        String bodyNoSpace = body.replaceAll("\\s+", "");
        if (bodyNoSpace.contains("return;") == false && body.contains("return")) {
          // Now check for 'return' followed by a value
          // This is a simple heuristic: look for 'return' followed by non-whitespace and
          // not immediately a semicolon
          int idx = body.indexOf("return");
          while (idx != -1) {
            int after = idx + 6;
            if (after < body.length() && body.charAt(after) != ';') {
              returnType = "I32";
              break;
            }
            idx = body.indexOf("return", idx + 1);
          }
        }
      }
      // Map return type
      String cReturnType = "void";
      if (!returnType.equals("Void")) {
        cReturnType = mapType(returnType);
      }
      // Compile body and find where function definition ends
      String cBody;
      String remainder = "";
      if (body.startsWith("{")) {
        // Find the matching closing brace
        int braceCount = 0;
        int bodyEnd = -1;
        for (int i = 0; i < body.length(); i++) {
          if (body.charAt(i) == '{') {
            braceCount++;
          } else if (body.charAt(i) == '}') {
            braceCount--;
            if (braceCount == 0) {
              bodyEnd = i + 1;
              break;
            }
          }
        }
        if (bodyEnd == -1) {
          throw new ApplicationException("Unmatched braces in function body");
        }
        cBody = body.substring(0, bodyEnd);
        remainder = body.substring(bodyEnd).trim();
      } else {
        cBody = "{" + body + "}";
      }
      output.append(cReturnType).append(" ").append(fnName).append("(").append(cParams).append(") ").append(cBody);

      // Process any remaining content after the function definition
      if (!remainder.isEmpty()) {
        output.append(" ");
        processStatement(remainder, output, context);
      }

      context.lastType = null;
      return;
    } else if (stmt.contains("=")) {
      processAssignmentStatement(stmt, output, context);
    } else if (stmt.contains("(") && stmt.contains(")")) {
      // Function call - output as-is with semicolon
      output.append(stmt).append(";");
      context.lastType = null;
      return;
    } else {
      throw new ApplicationException("This always throws an error.");
    }
  }

  // Helper for block scoping
  private String compileWithContext(String input, CompilationContext context) throws ApplicationException {
    if (input.isEmpty()) {
      return "";
    }
    String trimmed = input.trim();
    java.util.List<String> statements = new java.util.ArrayList<>();
    int bracketDepth = 0;
    int braceDepth = 0;
    StringBuilder current = new StringBuilder();
    for (int i = 0; i < trimmed.length(); i++) {
      char c = trimmed.charAt(i);
      if (c == '[')
        bracketDepth++;
      if (c == ']')
        bracketDepth--;
      if (c == '{')
        braceDepth++;
      if (c == '}')
        braceDepth--;
      if (c == ';' && bracketDepth == 0 && braceDepth == 0) {
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
    boolean first = true;
    for (String stmt : statements) {
      if (stmt.isEmpty()) {
        continue;
      }
      if (!first) {
        if (output.length() > 0 && output.charAt(output.length() - 1) != ' ') {
          output.append(" ");
        }
      }
      processStatement(stmt, output, context);
      first = false;
    }
    return output.toString().replaceAll("; +", "; ").trim();
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
    // Special case: let x = array.length; should assign array size
    if (valPart.endsWith(".length")) {
      String arrayName = valPart.substring(0, valPart.length() - 7);
      String outStr = output.toString();
      String arrSize = null;
      int idx = outStr.indexOf(arrayName + "[");
      if (idx != -1) {
        int start = idx + arrayName.length() + 1;
        int end = outStr.indexOf(']', start);
        if (end != -1) {
          arrSize = outStr.substring(start, end).trim();
        }
      }
      if (arrSize == null) {
        throw new ApplicationException("Cannot determine array size for '" + arrayName + "'");
      }
      // Check for explicit type declaration
      String explicitType = null;
      if (varPart.contains(":")) {
        String[] varSplit = varPart.split(":");
        explicitType = varSplit[1].trim();
        // Only allow assignment if type is compatible with usize_t
        if (!mapType(explicitType).equals("usize_t")) {
          throw new ApplicationException("Type mismatch: cannot assign array.length to " + explicitType);
        }
        varPart = varSplit[0].trim();
      }
      output.append("usize_t ").append(varPart).append(" = ").append(arrSize).append(";");
      context.lastType = "usize_t";
      return;
    }
    VariableDeclaration declaration = parseVariableDeclaration(varPart, valPart, context);
    output.append(declaration.type).append(" ").append(declaration.varName).append(" = ").append(declaration.value)
        .append(";");
    context.lastType = declaration.type;
    // Track variable in context
    String declaredName = declaration.varName;
    if (declaredName.contains("[")) {
      declaredName = declaredName.substring(0, declaredName.indexOf("["));
    }
    context.declareVariable(declaredName, declaration.type);
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
    if (!isCompatible(context.lastType, value, context)) {
      throw new ApplicationException("Type mismatch: cannot assign " + value + " to " + context.lastType);
    }
    output.append(" ").append(varName).append(" = ").append(value).append(";");
  }

  private VariableDeclaration parseVariableDeclaration(String varPart, String valPart, CompilationContext context)
      throws ApplicationException {
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
        // Support array initialization with elements or string literal
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
              if (!isCompatible(type, e, context)) {
                throw new ApplicationException("Type mismatch in array: cannot assign " + e + " to " + type);
              }
            }
            value = "{" + elements + "}";
          }
        } else if (valPart.startsWith("\"") && valPart.endsWith("\"")) {
          // String literal initialization for [U8; N]
          String str = valPart.substring(1, valPart.length() - 1);
          if (type.equals("uint8_t")) {
            if (str.length() != Integer.parseInt(arrSize)) {
              throw new ApplicationException("Array size mismatch: declared " + arrSize + ", got " + str.length());
            }
            StringBuilder asciiVals = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
              asciiVals.append((int) str.charAt(i));
              if (i < str.length() - 1)
                asciiVals.append(", ");
            }
            value = "{" + asciiVals.toString() + "}";
          } else {
            throw new ApplicationException("Invalid array initialization");
          }
        } else {
          throw new ApplicationException("Invalid array initialization");
        }
        // Skip type compatibility check for arrays
        return new VariableDeclaration(varName, type, value);
      } else {
        type = mapType(magmaType);
        // Type compatibility check
        if (!isCompatible(type, valPart, context)) {
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
        if (!isCompatible(type, e, context)) {
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
      if (!isCompatible(type, value, context)) {
        throw new ApplicationException("Type mismatch: cannot assign " + value + " to " + type);
      }
    }

    return new VariableDeclaration(varName, type, value);
  }

  private static class CompilationContext {
    String lastType = null;
    boolean isMut = false;
    java.util.Map<String, String> variables = new java.util.HashMap<>();
    CompilationContext parent = null;

    CompilationContext() {
    }

    CompilationContext(CompilationContext parent) {
      this.parent = parent;
    }

    String getVariableType(String name) {
      if (variables.containsKey(name))
        return variables.get(name);
      if (parent != null)
        return parent.getVariableType(name);
      return null;
    }

    void declareVariable(String name, String type) {
      variables.put(name, type);
    }
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
      case "USize":
        return "usize_t";
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
  private boolean isCompatible(String type, String value, CompilationContext context) {
    if (type.equals("bool")) {
      return isBooleanValue(value);
    }
    if (type.equals("uint8_t")) {
      return isUint8Compatible(value);
    }
    if ((type.startsWith("int") || type.startsWith("uint"))) {
      // Accept array element access like array[0] as compatible with int/uint types
      if (value.matches("[a-zA-Z_][a-zA-Z0-9_]*\\[\\d+\\]")) {
        return true;
      }
      // Accept variable names if declared and type matches
      if (value.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
        String varType = context != null ? context.getVariableType(value) : null;
        if (varType != null && varType.equals(type)) {
          return true;
        }
      }
      return isIntegerCompatible(value);
    }
    // Accept variable names for other types
    if (value.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
      String varType = context != null ? context.getVariableType(value) : null;
      if (varType != null && varType.equals(type)) {
        return true;
      }
    }
    return true;
  }

  private boolean isBooleanValue(String value) {
    if ("true".equals(value) || "false".equals(value)) {
      return true;
    }
    // Accept simple boolean expressions like 'true && false' or 'true || false'
    if (value.contains("&&")) {
      int idx = value.indexOf("&&");
      String left = value.substring(0, idx).trim();
      String right = value.substring(idx + 2).trim();
      if ((isBooleanValue(left)) && (isBooleanValue(right))) {
        return true;
      }
    } else if (value.contains("||")) {
      int idx = value.indexOf("||");
      String left = value.substring(0, idx).trim();
      String right = value.substring(idx + 2).trim();
      if ((isBooleanValue(left)) && (isBooleanValue(right))) {
        return true;
      }
    }
    // Accept comparison operators for boolean type
    String[] comparisons = { "==", "!=", "<=", ">=", "<", ">" };
    for (String cmp : comparisons) {
      int idx = value.indexOf(cmp);
      if (idx > 0) {
        String left = value.substring(0, idx).trim();
        String right = value.substring(idx + cmp.length()).trim();
        // Accept if both sides are integer-compatible
        if (isIntegerCompatible(left) && isIntegerCompatible(right)) {
          return true;
        }
      }
    }
    return false;
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

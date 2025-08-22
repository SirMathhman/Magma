package magma;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FunctionParser {
  private final String input;
  private String cur;
  private final StringBuilder rewritten = new StringBuilder();
  private final List<String> fnNames = new java.util.ArrayList<>();
  private final Map<String, Object[]> paramFns = new java.util.HashMap<>();

  public FunctionParser(String input) {
    this.input = input;
    this.cur = input;
  }

  public String parse() throws CompileException {
    while (cur.startsWith("fn ")) {
      parseFunction();
    }

    inlineFunctionCalls();

    return rewritten.toString() + cur;
  }

  private void parseFunction() throws CompileException {
    int i = 3;
    // parse function name up to '('
    StringBuilder name = new StringBuilder();
    while (i < cur.length() && cur.charAt(i) != '(') {
      char cc = cur.charAt(i);
      if (Character.isWhitespace(cc)) {
        i++;
        continue;
      }
      name.append(cc);
      i++;
    }
    if (name.length() == 0)
      throw new CompileException("Invalid function declaration in source: '" + input + "'");
    String fname = name.toString();
    int paramClose = cur.indexOf(')', i);
    if (paramClose == -1)
      throw new CompileException(
          "Invalid function declaration: missing ')' for '" + fname + "' in source: '" + input + "'");
    String paramList = cur.substring(i + 1, paramClose).trim();
    int arrow = cur.indexOf("=>", paramClose);
    if (arrow == -1)
      throw new CompileException(
          "Invalid function declaration, missing '=>' for '" + fname + "' in source: '" + input + "'");
    int semi = cur.indexOf(';', arrow);
    if (semi == -1)
      throw new CompileException(
          "Invalid function declaration: missing terminating ';' for '" + fname + "' in source: '" + input + "'");
    String body = cur.substring(arrow + 2, semi).trim();
    if (paramList.isEmpty()) {
      // zero-arg: translate to let binding
      rewritten.append("let ").append(fname).append(" = ").append(body).append("; ");
      fnNames.add(fname);
    } else {
      List<Parameter> parameters = new ArrayList<>();
      String[] params = paramList.split(",");
      for (String p : params) {
        int colon = p.indexOf(':');
        if (colon == -1)
          throw new CompileException("Invalid parameter '" + p + "', missing type declaration.");
        String paramName = p.substring(0, colon).trim();
        String type = p.substring(colon + 1).trim();
        parameters.add(new Parameter(paramName, type));
      }
      paramFns.put(fname, new Object[] { body, parameters });
    }
    cur = cur.substring(semi + 1).trim();
    // replace zero-arg calls in the remaining cur to use identifier form
    for (String n : fnNames) {
      cur = cur.replace(n + "()", n);
    }
  }

  private void inlineFunctionCalls() throws CompileException {
    for (Map.Entry<String, Object[]> e : paramFns.entrySet()) {
      String fname = e.getKey();
      Object[] value = e.getValue();
      String body = (String) value[0];
      List<Parameter> parameters = (List<Parameter>) value[1];

      int idx = cur.indexOf(fname + "(");
      while (idx != -1) {
        int start = idx + fname.length() + 1; // index of arg start
        int close = cur.indexOf(')', start);
        if (close == -1)
          throw new CompileException("Invalid call to function '" + fname + "' missing ')' in source: '" + input + "'");
        String argsList = cur.substring(start, close).trim();
        String[] args = argsList.split(",");
        if (args.length != parameters.size()) {
          throw new CompileException("Mismatched argument count for function " + fname);
        }
        String inlined = body;
        for (int i = 0; i < parameters.size(); i++) {
          String arg = args[i].trim();
          Parameter parameter = parameters.get(i);
          String paramType = parameter.getType();
          if (paramType.equals("I32")) {
            try {
              Integer.parseInt(arg);
            } catch (NumberFormatException ex) {
              if (arg.equals("true") || arg.equals("false")) {
                throw new CompileException(
                    "Mismatched type for parameter " + parameter.getName() + ", expected I32 but got Bool");
              } else {
                throw new CompileException(
                    "Mismatched type for parameter " + parameter.getName() + ", expected I32 but got " + arg);
              }
            }
          } else if (paramType.equals("Bool")) {
            if (!arg.equals("true") && !arg.equals("false")) {
              throw new CompileException(
                  "Mismatched type for parameter " + parameter.getName() + ", expected Bool but got " + arg);
            }
          }
          inlined = replaceIdent(inlined, parameter.getName(), arg);
        }
        cur = cur.substring(0, idx) + inlined + cur.substring(close + 1);
        idx = cur.indexOf(fname + "(");
      }
    }
  }

  private static String replaceIdent(String src, String ident, String repl) {
    StringBuilder out = new StringBuilder();
    int i = 0;
    while (i < src.length()) {
      char c = src.charAt(i);
      if (Character.isJavaIdentifierStart(c)) {
        int j = i;
        StringBuilder id = new StringBuilder();
        while (j < src.length() && Character.isJavaIdentifierPart(src.charAt(j))) {
          id.append(src.charAt(j));
          j++;
        }
        if (id.toString().equals(ident)) {
          out.append(repl);
        } else {
          out.append(id.toString());
        }
        i = j;
      } else {
        out.append(c);
        i++;
      }
    }
    return out.toString();
  }
}

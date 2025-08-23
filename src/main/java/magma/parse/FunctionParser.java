package magma.parse;

import magma.core.CompileException;
import magma.core.Parameter;
import magma.util.ExprUtils;

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

  private static List<Parameter> parseParameters(String paramList) throws CompileException {
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
    return parameters;
  }

  public String parse() throws CompileException {
    while (cur.startsWith("fn ")) {
      parseFunction();
    }
    // If a let binding aliases a parameterized function (e.g. let f : (I32) => I32
    // = get;)
    // rewrite subsequent calls to the alias (f(...)) to call the original function
    // (get(...)).
    for (String fname : paramFns.keySet()) {
      int scan = 0;
      while (true) {
        int letIdx = cur.indexOf("let ", scan);
        if (letIdx == -1)
          break;
        int i = letIdx + 4;
        // skip optional mut
        if (cur.startsWith("mut ", i))
          i += 4;
        ExprUtils.IdentResult aliasRes = ExprUtils.collectIdentifierResult(cur, i);
        if (aliasRes.ident.isEmpty()) {
          scan = letIdx + 4;
          continue;
        }
        String alias = aliasRes.ident;
        i = aliasRes.idx;
        int eq;
        try {
          eq = ExprUtils.findAssignmentIndex(cur, i);
        } catch (CompileException ce) {
          scan = letIdx + 4;
          continue;
        }
        int semi = cur.indexOf(';', eq);
        if (semi == -1) {
          scan = letIdx + 4;
          continue;
        }
        String declExpr = cur.substring(eq + 1, semi).trim();
        if (declExpr.equals(fname)) {
          // remove the let alias declaration entirely and rewrite call sites
          String a = alias;
          // remove the 'let ... = ...;' segment
          cur = cur.substring(0, letIdx) + cur.substring(semi + 1);
          // rewrite calls to alias to call the original function
          cur = cur.replace(a + "(", fname + "(");
          // continue scanning at the position where the let was removed
          scan = letIdx;
          continue;
        }
        scan = semi + 1;
      }
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
    String returnType = parseReturnType(cur, paramClose, arrow);
    String body = cur.substring(arrow + 2, semi).trim();

    // basic return type check: declared I32 must not return Bool literal
    if (returnType != null && "I32".equals(returnType) && ("true".equals(body) || "false".equals(body))) {
      throw new CompileException(
          "Mismatched return type for function " + fname + ", declared I32 but body is Bool");
    }
    if (paramList.isEmpty()) {
      // zero-arg: translate to let binding
      rewritten.append("let ").append(fname).append(" = ").append(body).append("; ");
      fnNames.add(fname);
    } else {
      List<Parameter> parameters = parseParameters(paramList);
      paramFns.put(fname, new Object[] { body, parameters, returnType });
    }
    cur = cur.substring(semi + 1).trim();
    // replace zero-arg calls in the remaining cur to use identifier form
    for (String n : fnNames) {
      cur = cur.replace(n + "()", n);
    }
  }

  private static String parseReturnType(String cur, int paramClose, int arrow) {
    String between = cur.substring(paramClose + 1, arrow).trim();
    if (between.startsWith(":")) {
      String rt = between.substring(1).trim();
      if (!rt.isEmpty())
        return rt;
    }
    return null;
  }

  private void inlineFunctionCalls() throws CompileException {
    for (Map.Entry<String, Object[]> e : paramFns.entrySet()) {
      String fname = e.getKey();
      Object[] value = e.getValue();
      String body = (String) value[0];
      @SuppressWarnings("unchecked")
      List<Parameter> parameters = (List<Parameter>) value[1];
      // returnType is stored in value[2] for future use if needed

      int idx = cur.indexOf(fname + "(");
      while (idx != -1) {
        int open = idx + fname.length(); // index of '('
        int start = open + 1; // index of arg start
        int close = findMatchingParen(cur, open);
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
              // Accept call expressions like readInt() or other parenthesized expressions as
              // valid I32 arguments here; they will be further parsed later.
              if (arg.equals("true") || arg.equals("false")) {
                throw new CompileException(
                    "Mismatched type for parameter " + parameter.getName() + ", expected I32 but got Bool");
              } else if (arg.endsWith(")") && arg.indexOf('(') != -1) {
                // treat as an expression (e.g. readInt()) and accept for now
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

  private static int findMatchingParen(String s, int openIdx) {
    int len = s.length();
    if (openIdx < 0 || openIdx >= len || s.charAt(openIdx) != '(')
      return -1;
    int depth = 1;
    int i = openIdx + 1;
    while (i < len) {
      char c = s.charAt(i);
      if (c == '(')
        depth++;
      else if (c == ')') {
        depth--;
        if (depth == 0)
          return i;
      }
      i++;
    }
    return -1;
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

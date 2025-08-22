package magma;

import java.util.List;
import java.util.Map;

public class FunctionParser {
  private final String input;
  private String cur;
  private final StringBuilder rewritten = new StringBuilder();
  private final List<String> fnNames = new java.util.ArrayList<>();
  private final Map<String, String[]> paramFns = new java.util.HashMap<>();

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
      java.util.List<String> paramNames = new java.util.ArrayList<>();
      String[] params = paramList.split(",");
      for (String p : params) {
        int colon = p.indexOf(':');
        String paramName = colon == -1 ? p.trim() : p.substring(0, colon).trim();
        paramNames.add(paramName);
      }
      String[] value = new String[1 + paramNames.size()];
      value[0] = body;
      for (int j = 0; j < paramNames.size(); j++) {
        value[j + 1] = paramNames.get(j);
      }
      paramFns.put(fname, value);
    }
    cur = cur.substring(semi + 1).trim();
    // replace zero-arg calls in the remaining cur to use identifier form
    for (String n : fnNames) {
      cur = cur.replace(n + "()", n);
    }
  }

  private void inlineFunctionCalls() throws CompileException {
    for (java.util.Map.Entry<String, String[]> e : paramFns.entrySet()) {
      String fname = e.getKey();
      String[] value = e.getValue();
      String body = value[0];
      java.util.List<String> paramNames = new java.util.ArrayList<>();
      for (int i = 1; i < value.length; i++) {
        paramNames.add(value[i]);
      }

      int idx = cur.indexOf(fname + "(");
      while (idx != -1) {
        int start = idx + fname.length() + 1; // index of arg start
        int close = cur.indexOf(')', start);
        if (close == -1)
          throw new CompileException("Invalid call to function '" + fname + "' missing ')' in source: '" + input + "'");
        String argsList = cur.substring(start, close).trim();
        String[] args = argsList.split(",");
        if (args.length != paramNames.size()) {
          throw new CompileException("Mismatched argument count for function " + fname);
        }
        String inlined = body;
        for (int i = 0; i < paramNames.size(); i++) {
          inlined = replaceIdent(inlined, paramNames.get(i), args[i].trim());
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

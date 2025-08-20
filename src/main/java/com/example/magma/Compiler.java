package com.example.magma;

import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

public final class Compiler {
  public static String compile(String source) {
    // Find the last non-empty trimmed line using pure string operations
    // (no IO utilities, no regexes).
    String lastLine = "";
    StringBuilder cur = new StringBuilder();
    for (int i = 0, n = source.length(); i < n; i++) {
      char c = source.charAt(i);
      if (c == '\r' || c == '\n') {
        String trimmed = cur.toString().trim();
        if (!trimmed.isEmpty()) {
          lastLine = trimmed;
        }
        cur.setLength(0);
      } else {
        cur.append(c);
      }
    }
    // Last segment after final newline (or whole string if no newlines).
    if (cur.length() > 0) {
      String trimmed = cur.toString().trim();
      if (!trimmed.isEmpty()) {
        lastLine = trimmed;
      }
    }

    String expr = Optional.of(lastLine).filter(s -> !s.isEmpty()).orElse("0");

    // If the expression is a simple let-binding like
    // let x = <init>; <rest>
    // translate it into C by declaring the variable then returning the
    // rest expression. We only use plain string operations (no regex).
    String preMain = "#include <stdio.h>\n" +
        "int readInt(void) {\n" +
        "  int x = 0;\n" +
        "  if (scanf(\"%d\", &x) != 1) return 0;\n" +
        "  return x;\n" +
        "}\n\n";

    // Support multiple sequential let-bindings such as
    // let x = ...; let y = ...; expr
    // by extracting each binding and emitting a C declaration before the
    // final return. Use only string operations.
    StringBuilder decls = new StringBuilder();
    Set<String> declared = new HashSet<>();
    Set<String> mutable = new HashSet<>();
    Set<String> callableVars = new HashSet<>();
    Map<String, String> types = new HashMap<>();
    Map<String, Integer> functions = new HashMap<>();
    Map<String, String[]> functionParamTypes = new HashMap<>();
    // builtin extern functions (prelude) we accept — map name to arity
    functions.put("readInt", 0);
    StringBuilder functionDefs = new StringBuilder();
    while (expr.startsWith("let ")) {
      int eq = expr.indexOf('=');
      int sem = expr.indexOf(';', eq >= 0 ? eq : 0);
      if (!(eq > 0 && sem > eq)) {
        break; // malformed let; fall back to returning whatever remains
      }
      String namePart = expr.substring(4, eq).trim();
      boolean isMutable = false;
      if (namePart.startsWith("mut ")) {
        isMutable = true;
        namePart = namePart.substring(4).trim();
      }
      int colonIdx = namePart.indexOf(':');
      String name = colonIdx >= 0 ? namePart.substring(0, colonIdx).trim() : namePart;
      String typeStr = colonIdx >= 0 ? namePart.substring(colonIdx + 1).trim() : "";
      String initExpr = expr.substring(eq + 1, sem).trim();

      // Simple type checks: ensure booleans aren't assigned to I32 and
      // basic Bool assignments are booleans. Also record the declared
      // or inferred type for later assignment checks.
      String declaredType;
      if (!typeStr.isEmpty()) {
        declaredType = typeStr;
        if ("I32".equals(typeStr)) {
          if ("true".equals(initExpr) || "false".equals(initExpr)) {
            throw new CompileException("Type mismatch: cannot assign boolean to I32");
          }
        } else if ("Bool".equals(typeStr)) {
          if (!("true".equals(initExpr) || "false".equals(initExpr))) {
            throw new CompileException("Type mismatch: Bool must be assigned a boolean literal");
          }
        }
      } else {
        // Infer type from initializer: boolean literals -> Bool, otherwise I32
        if ("true".equals(initExpr) || "false".equals(initExpr)) {
          declaredType = "Bool";
        } else {
          declaredType = "I32";
        }
      }

      // Map plain boolean literals to integers for C early
      if ("true".equals(initExpr)) {
        initExpr = "1";
      } else if ("false".equals(initExpr)) {
        initExpr = "0";
      }

      // initializer trimmed (not used further)
      // Validate any identifiers referenced in the initializer are declared
      // (e.g. `let x = y;` where y must already be declared). Allow calls
      // to functions as well.
      validateIdentifiers(initExpr, declared, functions, types, functionParamTypes, callableVars);

      if (declared.contains(name)) {
        throw new CompileException("Duplicate variable: " + name);
      }
      // If the initializer is a call to a zero-arg function, emit a
      // wrapper function with the variable's name so later calls like
      // `func()` are valid C function calls. This keeps semantics simple
      // and avoids emitting an int variable that would make `func()` a
      // compile-time error in C.
      int parenIdx = initExpr.indexOf('(');
      boolean emittedWrapper = false;
      // Only consider emitting a wrapper if initializer is a zero-arg call to a known
      // function
      // and the variable name is later used as a call in the remaining expression.
      if (parenIdx > 0 && initExpr.endsWith(")")) {
        String possibleFn = initExpr.substring(0, parenIdx).trim();
        Integer ar = functions.get(possibleFn);
        if (ar != null && ar == 0) {
          String rest = expr.substring(sem + 1);
          if (isCalledLater(name, rest)) {
            if (declared.contains(name) || functions.containsKey(name)) {
              throw new CompileException("Duplicate function or variable: " + name);
            }
            functions.put(name, 0);
            functionParamTypes.put(name, new String[0]);
            functionDefs.append("int ").append(name).append("() {\n");
            functionDefs.append("  return (").append(normalize(initExpr)).append(");\n");
            functionDefs.append("}\n\n");
            emittedWrapper = true;
          }
        }
      }
      if (!emittedWrapper) {
        declared.add(name);
        if (isMutable) {
          mutable.add(name);
        }
        types.put(name, declaredType);
        decls.append("  int ").append(name).append(" = (").append(normalize(initExpr)).append(");\n");
      }
      expr = expr.substring(sem + 1).trim();
    }

    // The final expression and body will be computed below after handling
    // statements; leave expr intact for parsing.
    // Split remaining expr into statements separated by ';'. Any statements
    // (assignments) before the final expression are emitted as statements in
    // the function body; the last segment (if any) becomes the returned
    // expression. Validate assignments against mutability rules.
    StringBuilder body = new StringBuilder();
    String remaining = expr;
    String lastSegment = null;
    while (true) {
      int idx = remaining.indexOf(';');
      if (idx < 0) {
        lastSegment = remaining.trim();
        break;
      }
      String stmt = remaining.substring(0, idx).trim();
      if (!stmt.isEmpty()) {
        // validate assignment statements like: name = expr
        // Handle function declarations of the form: fn name() => expr
        if (stmt.startsWith("fn ")) {
          int nameStart = 3;
          int paren = stmt.indexOf('(', nameStart);
          if (paren < 0) {
            throw new CompileException("Malformed function declaration: " + stmt);
          }
          String fname = stmt.substring(nameStart, paren).trim();
          // strip generic type params like <T> from the function name
          int lt = fname.indexOf('<');
          if (lt >= 0) {
            fname = fname.substring(0, lt).trim();
          }
          int closeParen = stmt.indexOf(')', paren);
          if (closeParen < 0) {
            throw new CompileException("Malformed function declaration: " + stmt);
          }
          int arrow = stmt.indexOf("=>", closeParen + 1);
          if (arrow < 0) {
            throw new CompileException("Malformed function declaration: " + stmt);
          }
          String params = stmt.substring(paren + 1, closeParen).trim();
          String fbody = stmt.substring(arrow + 2).trim();
          if ("true".equals(fbody)) {
            fbody = "1";
          } else if ("false".equals(fbody)) {
            fbody = "0";
          }
          // parse parameters list into names and types
          StringBuilder cParamList = new StringBuilder();
          Set<String> paramNames = new HashSet<>();
          java.util.List<String> paramTypesList = new java.util.ArrayList<>();
          if (!params.isEmpty()) {
            String[] parts = params.split(",");
            for (int pi = 0; pi < parts.length; pi++) {
              String p = parts[pi].trim();
              int colon = p.indexOf(':');
              if (colon < 0) {
                throw new CompileException("Malformed parameter: " + p);
              }
              String pname = p.substring(0, colon).trim();
              String ptype = p.substring(colon + 1).trim();
              if (pname.isEmpty() || ptype.isEmpty()) {
                throw new CompileException("Malformed parameter: " + p);
              }
              if (paramNames.contains(pname)) {
                throw new CompileException("Duplicate parameter name: " + pname);
              }
              paramNames.add(pname);
              paramTypesList.add(ptype);
              // only I32/Bool supported; map both to C int
              if (cParamList.length() > 0)
                cParamList.append(", ");
              cParamList.append("int ").append(pname);
            }
          }
          // Validate identifiers used in function body with params in scope
          Set<String> declaredWithParams = new HashSet<>(declared);
          declaredWithParams.addAll(paramNames);
          validateIdentifiers(fbody, declaredWithParams, functions, types, functionParamTypes, new HashSet<String>());
          if (declared.contains(fname) || functions.containsKey(fname)) {
            throw new CompileException("Duplicate function or variable: " + fname);
          }
          functions.put(fname, paramNames.size());
          functionParamTypes.put(fname, paramTypesList.toArray(new String[0]));
          functionDefs.append("int ").append(fname).append("(");
          functionDefs.append(cParamList.toString());
          functionDefs.append(") {\n");
          functionDefs.append("  return (").append(normalize(fbody)).append(");\n");
          functionDefs.append("}\n\n");
        } else {
          // find an assignment '=' that is not part of the '=>' arrow
          int assignIdx = -1;
          for (int k = 0; k < stmt.length(); k++) {
            if (stmt.charAt(k) == '=') {
              if (k + 1 < stmt.length() && stmt.charAt(k + 1) == '>') {
                continue; // part of '=>' arrow
              }
              assignIdx = k;
              break;
            }
          }
          if (assignIdx > 0) {
            String lhs = stmt.substring(0, assignIdx).trim();
            String rhs = stmt.substring(assignIdx + 1).trim();
            validateAssignment(lhs, rhs, declared, mutable, types, functions, functionParamTypes);
          }
          // For non-assignment expression statements, ensure identifiers exist
          if (assignIdx < 0) {
            validateIdentifiers(stmt, declared, functions, types, functionParamTypes, callableVars);
          }
          body.append("  ").append(normalize(stmt)).append(";\n");
        }
      }
      remaining = remaining.substring(idx + 1).trim();
      if (remaining.isEmpty()) {
        lastSegment = "";
        break;
      }
    }

    String finalExpr = (lastSegment == null || lastSegment.isEmpty()) ? "0" : lastSegment;
    if ("true".equals(finalExpr)) {
      finalExpr = "1";
    } else if ("false".equals(finalExpr)) {
      finalExpr = "0";
    } else {
      // If final expression is an assignment, validate mutability too.
      int assignIdx = finalExpr.indexOf('=');
      if (assignIdx > 0) {
        String lhs = finalExpr.substring(0, assignIdx).trim();
        String rhs = finalExpr.substring(assignIdx + 1).trim();
        validateAssignment(lhs, rhs, declared, mutable, types, functions, functionParamTypes);
      }
      // Validate identifiers used in the final expression as well.
      validateIdentifiers(finalExpr, declared, functions, types, functionParamTypes, callableVars);
    }
    finalExpr = normalize(finalExpr);

    // If the final expression is a single bare identifier (e.g. "readInt")
    // then it must refer to a declared variable. Reject bare function
    // references or undeclared identifiers rather than emitting invalid C
    // (which would produce a compiler/runtime error).
    String feTrim = finalExpr.trim();
    if (!feTrim.isEmpty()) {
      // detect a single identifier token: starts with letter/_ then letters/digits/_
      // only
      char first = feTrim.charAt(0);
      if (Character.isLetter(first) || first == '_') {
        boolean allIdent = true;
        for (int i = 1; i < feTrim.length(); i++) {
          char c = feTrim.charAt(i);
          if (!(Character.isLetterOrDigit(c) || c == '_')) {
            allIdent = false;
            break;
          }
        }
        if (allIdent) {
          // it's a bare identifier token; ensure it's a declared variable
          if (!declared.contains(feTrim)) {
            throw new CompileException("Use of undefined identifier: " + feTrim);
          }
        }
      }
    }

    return preMain +
        functionDefs.toString() +
        "int main(void) {\n" +
        decls.toString() +
        body.toString() +
        "  return (" + finalExpr + ");\n" +
        "}\n";
  }

  private static void validateAssignment(String lhs, String rhs, Set<String> declared, Set<String> mutable,
      Map<String, String> types, Map<String, Integer> functions, Map<String, String[]> functionParamTypes) {
    if (!declared.contains(lhs)) {
      throw new CompileException("Assignment to undeclared variable: " + lhs);
    }
    if (!mutable.contains(lhs)) {
      throw new CompileException("Assignment to immutable variable: " + lhs);
    }
    // Ensure any identifiers referenced on the RHS are declared.
    validateIdentifiers(rhs, declared, functions, types, functionParamTypes, new HashSet<String>());
    String declaredType = types.get(lhs);
    if (declaredType != null) {
      if ("I32".equals(declaredType)) {
        if ("true".equals(rhs) || "false".equals(rhs)) {
          throw new CompileException("Type mismatch: cannot assign boolean to I32");
        }
      } else if ("Bool".equals(declaredType)) {
        if (!("true".equals(rhs) || "false".equals(rhs))) {
          throw new CompileException("Type mismatch: Bool must be assigned a boolean literal");
        }
      }
    }
  }

  // Lightweight normalization to ensure emitted C uses 0/1 for booleans and
  // retains ternary `if (` sequences as-is. This is intentionally small and
  // uses only plain string operations.
  private static String normalize(String s) {
    if (s == null)
      return "";
    String out = s.trim();
    if ("true".equals(out))
      return "1";
    if ("false".equals(out))
      return "0";
    // Replace boolean literals occurring as separate tokens with 1/0.
    // Scan and rebuild to avoid accidental replacement inside identifiers.
    StringBuilder b = new StringBuilder();
    int i = 0, n = out.length();
    while (i < n) {
      char c = out.charAt(i);
      if (Character.isLetter(c) || c == '_') {
        int j = i + 1;
        while (j < n && (Character.isLetterOrDigit(out.charAt(j)) || out.charAt(j) == '_'))
          j++;
        String tok = out.substring(i, j);
        if ("true".equals(tok))
          b.append('1');
        else if ("false".equals(tok))
          b.append('0');
        else if ("if".equals(tok)) {
          // drop the 'if' token for ternary expressions ("if (cond) ? a : b" -> "(cond) ?
          // a : b")
        } else
          b.append(tok);
        i = j;
      } else {
        b.append(c);
        i++;
      }
    }
    return b.toString();
  }

  private static boolean isCalledLater(String name, String rest) {
    if (rest == null || rest.isEmpty())
      return false;
    // simple scan: look for the token name followed by optional whitespace and '('
    int n = rest.length();
    for (int i = 0; i < n; i++) {
      char c = rest.charAt(i);
      if (Character.isLetter(c) || c == '_') {
        int j = i + 1;
        while (j < n && (Character.isLetterOrDigit(rest.charAt(j)) || rest.charAt(j) == '_'))
          j++;
        String tok = rest.substring(i, j);
        if (tok.equals(name)) {
          int k = j;
          while (k < n && Character.isWhitespace(rest.charAt(k)))
            k++;
          if (k < n && rest.charAt(k) == '(')
            return true;
        }
        i = j - 1;
      }
    }
    return false;
  }

  private static void validateIdentifiers(String expr, Set<String> declared, Map<String, Integer> functions,
      Map<String, String> types, Map<String, String[]> functionParamTypes, Set<String> callableVars) {
    if (expr == null || expr.trim().isEmpty()) {
      return;
    }
    // Scan the string for identifier-like tokens (start with letter or '_', then
    // letters/digits/_)
    int n = expr.length();
    StringBuilder token = new StringBuilder();
    for (int i = 0; i <= n; i++) {
      char c = i < n ? expr.charAt(i) : '\0';
      if (Character.isLetter(c) || c == '_') {
        token.append(c);
        continue;
      }
      if (Character.isDigit(c)) {
        if (token.length() > 0) {
          token.append(c);
          continue;
        }
        // digit starting token -> numeric literal, skip
        while (i < n && Character.isDigit(expr.charAt(i)))
          i++;
        i--; // adjust because loop will i++
        token.setLength(0);
        continue;
      }
      // non-identifier char ends a token
      if (token.length() > 0) {
        String t = token.toString();
        // Check whether this identifier is immediately used as a call, i.e.
        // followed (possibly after whitespace) by '('. If a declared variable
        // is used as a function call, that's a compile error (e.g. let x=1; x()).
        boolean isCall = false;
        int j = i;
        while (j < n && Character.isWhitespace(expr.charAt(j)))
          j++;
        if (j < n && expr.charAt(j) == '(') {
          isCall = true;
        }

        // allow 'if' as part of ternary expressions
        if ("if".equals(t)) {
          token.setLength(0);
          continue;
        }

        // allowed identifiers: boolean literals and builtins
        if (!"true".equals(t) && !"false".equals(t) && !functions.containsKey(t)) {
          if (!declared.contains(t) && !functions.containsKey(t)) {
            throw new CompileException("Use of undefined identifier: " + t);
          }
        }

        // If this token is a call and the identifier is a declared variable,
        // that's an error: calling a non-function. If it's a function name,
        // check arity and argument types.
        if (isCall) {
          // If a variable with the same name exists it shadows any function
          // for the purposes of calls and is only callable if marked as a
          // callable variable (initialized from a zero-arg function call).
          if (declared.contains(t) && !callableVars.contains(t)) {
            throw new CompileException("Call of non-function identifier: " + t);
          }
          if (functions.containsKey(t)) {
            // find matching closing paren and extract args substring
            int argStart = j + 1;
            int depth = 1;
            int end = argStart;
            for (int k = argStart; k < n; k++) {
              char cc = expr.charAt(k);
              if (cc == '(')
                depth++;
              else if (cc == ')') {
                depth--;
                if (depth == 0) {
                  end = k;
                  break;
                }
              }
            }
            String argsSub = expr.substring(argStart, end);
            // split top-level commas
            java.util.List<String> args = new java.util.ArrayList<>();
            int depth2 = 0;
            StringBuilder curArg = new StringBuilder();
            for (int k = 0; k < argsSub.length(); k++) {
              char cc = argsSub.charAt(k);
              if (cc == '(') {
                depth2++;
                curArg.append(cc);
              } else if (cc == ')') {
                depth2--;
                curArg.append(cc);
              } else if (cc == ',' && depth2 == 0) {
                args.add(curArg.toString().trim());
                curArg.setLength(0);
              } else {
                curArg.append(cc);
              }
            }
            if (curArg.length() > 0)
              args.add(curArg.toString().trim());

            // arity checking
            Integer expected = functions.get(t);
            if (expected == null)
              expected = -1;
            if (expected >= 0 && expected != args.size()) {
              throw new CompileException("Wrong number of arguments to function: " + t);
            }

            // simple parameter type checking when types are known
            String[] paramTypes = functionParamTypes.get(t);
            if (paramTypes != null) {
              for (int ai = 0; ai < args.size() && ai < paramTypes.length; ai++) {
                String at = args.get(ai).trim();
                String ptype = paramTypes[ai];
                if ("I32".equals(ptype)) {
                  if ("true".equals(at) || "false".equals(at)) {
                    throw new CompileException("Type mismatch: cannot pass boolean to I32 parameter of " + t);
                  }
                } else if ("Bool".equals(ptype)) {
                  if (!("true".equals(at) || "false".equals(at))) {
                    // if arg is an identifier with a known type, accept only Bool
                    String atType = types.get(at);
                    if (atType == null || !"Bool".equals(atType)) {
                      throw new CompileException("Type mismatch: expected Bool for parameter " + ai + " of " + t);
                    }
                  }
                }
              }
            }
          }
        }

        token.setLength(0);
      }
      // otherwise skip this character
    }
  }
}

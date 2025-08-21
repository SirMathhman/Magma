package com.example.magma;

import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

public final class Compiler {
  // Small lexical helpers (pure, no regex):
  private static boolean isIdentStart(char c) {
    return Character.isLetter(c) || c == '_';
  }

  private static boolean isIdentPart(char c) {
    return Character.isLetterOrDigit(c) || c == '_';
  }

  private static int scanIdentEnd(String s, int start) {
    int n = s.length();
    int j = start + 1;
    while (j < n && isIdentPart(s.charAt(j)))
      j++;
    return j;
  }

  private static int skipWhitespace(String s, int i) {
    int n = s.length();
    while (i < n && Character.isWhitespace(s.charAt(i)))
      i++;
    return i;
  }

  private static int findStandaloneToken(String s, String token, int from) {
    int n = s.length();
    for (int i = from; i < n; i++) {
      char c = s.charAt(i);
      if (isIdentStart(c)) {
        int j = scanIdentEnd(s, i);
        String tok = s.substring(i, j);
        if (token.equals(tok)) {
          boolean leftOk = (i == 0) || !isIdentPart(s.charAt(i - 1));
          boolean rightOk = (j >= n) || !isIdentPart(s.charAt(j));
          if (leftOk && rightOk)
            return i;
        }
        i = j - 1;
      }
    }
    return -1;
  }

  private static int findCondEndAtDepthZero(String s, int start) {
    int n = s.length();
    int i = start;
    while (i < n) {
      char c = s.charAt(i);
      if (c == '(') {
        int match = findMatching(s, i, '(', ')');
        if (match < 0)
          return n;
        i = match + 1;
        continue;
      }
      if (Character.isWhitespace(c))
        return i;
      i++;
    }
    return n;
  }

  // Find an '=' character that is not part of the '=>' arrow. Return -1 if
  // none found from 'from' onward.
  private static int findAssignEquals(String s, int from) {
    for (int i = Math.max(0, from); i < s.length(); i++) {
      if (s.charAt(i) == '=') {
        // skip '=>' arrow
        if (i + 1 < s.length() && s.charAt(i + 1) == '>')
          continue;
        // skip '==' equality (either side)
        if (i + 1 < s.length() && s.charAt(i + 1) == '=')
          continue;
        if (i - 1 >= 0 && s.charAt(i - 1) == '=')
          continue;
        // skip part of '<=', '>=', '!=' where '=' is second char
        if (i - 1 >= 0) {
          char prev = s.charAt(i - 1);
          if (prev == '<' || prev == '>' || prev == '!')
            continue;
        }
        return i;
      }
    }
    return -1;
  }

  private static void emitWrapperFunction(String name, String callExpr, String[] paramTypes, Set<String> declared,
      Map<String, Integer> functions, Map<String, String[]> functionParamTypes, StringBuilder functionDefs) {
    if (declared.contains(name) || functions.containsKey(name)) {
      throw new CompileException("Duplicate function or variable: " + name);
    }
    functions.put(name, 0);
    functionParamTypes.put(name, paramTypes);
    functionDefs.append("int ").append(name).append("() {\n");
    functionDefs.append("  return (").append(callExpr).append(");\n");
    functionDefs.append("}\n\n");
  }

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
        "  if (scanf(\"%d\", &x) != 1) return 5;\n" +
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
    StringBuilder structDefs = new StringBuilder();
    StringBuilder functionDefs = new StringBuilder();
    Map<String, java.util.List<String>> structFieldNames = new HashMap<>();
    Map<String, java.util.List<String>> structFieldTypes = new HashMap<>();
    while (expr.startsWith("let ")) {
      int eq = findAssignEquals(expr, 0);
      int sem = expr.indexOf(';', eq >= 0 ? eq : 0);
      if (!(eq > 0 && sem > eq)) {
        break; // malformed let; fall back to returning whatever remains
      }
      String namePart = expr.substring(4, eq).trim();
      String initExpr = expr.substring(eq + 1, sem).trim();
      LetInfo info = parseLetHeader(namePart, initExpr, structFieldNames);
      String name = info.name; // parseLetHeader keeps name normalized
      boolean isMutable = info.isMutable;
      String declaredType = info.declaredType;
      // Find ':' separating name and optional type (skip any ':' inside nested
      // generics)
      int colonIdxTop = -1;
      for (int ci = 0; ci < namePart.length(); ci++) {
        char cc = namePart.charAt(ci);
        if (cc == ':') {
          colonIdxTop = ci;
          break;
        }
      }
      String explicitTypeTop = colonIdxTop >= 0 ? namePart.substring(colonIdxTop + 1).trim() : "";
      if (!explicitTypeTop.isEmpty()) {
        checkExplicitTypeCompatibility(explicitTypeTop, initExpr);
      }

      // If the explicit type is a function type like "(...) => T", make the
      // let-bound name callable. If the initializer is a reference to an
      // existing zero-arg function, emit a wrapper function so calling the
      // name works as expected; otherwise mark the variable as a callable
      // variable so validateIdentifiers allows calls.
      boolean handledAsFunctionType = false;
      if (!explicitTypeTop.isEmpty() && explicitTypeTop.contains("=>")) {
        int arrow = explicitTypeTop.indexOf("=>");
        String paramsPart = explicitTypeTop.substring(0, arrow).trim();
        if (paramsPart.startsWith("(") && paramsPart.endsWith(")")) {
          paramsPart = paramsPart.substring(1, paramsPart.length() - 1).trim();
        }
        String[] paramTypesArr;
        if (paramsPart.isEmpty()) {
          paramTypesArr = new String[0];
        } else {
          String[] raw = paramsPart.split(",");
          paramTypesArr = new String[raw.length];
          for (int ri = 0; ri < raw.length; ri++)
            paramTypesArr[ri] = raw[ri].trim();
        }
        // arity currently unused beyond basic wrapper decision

        // If the variable is mutable we cannot emit a fixed wrapper function
        // at let-time because it may be reassigned later; instead declare a
        // callable variable placeholder so assignments like `f = () => 42;`
        // can be handled when they occur. For immutable lets, emit a
        // wrapper function immediately.
        if (isMutable) {
          // declare a placeholder callable variable (use 0 as placeholder)
          declared.add(name);
          mutable.add(name);
          types.put(name, explicitTypeTop);
          callableVars.add(name);
          // placeholder initializer — actual function may be assigned later
          decls.append("  int ").append(name).append(" = (0);");
          decls.append("\n");
          handledAsFunctionType = true;
        } else {
          // If initializer names a known zero-arg function, create a wrapper
          // function; otherwise create a wrapper around the normalized
          // initializer expression.
          String possibleFnName = initExpr;
          int pidx = possibleFnName.indexOf('(');
          if (pidx > 0)
            possibleFnName = possibleFnName.substring(0, pidx).trim();
          Integer existingAr = functions.get(possibleFnName);
          if (existingAr != null && existingAr == 0) {
            String callExpr = possibleFnName + (initExpr.endsWith(")") ? "" : "()");
            emitWrapperFunction(name, callExpr, paramTypesArr, declared, functions, functionParamTypes,
                functionDefs);
            handledAsFunctionType = true;
          } else {
            emitWrapperFunction(name, normalize(initExpr), paramTypesArr, declared, functions, functionParamTypes,
                functionDefs);
            handledAsFunctionType = true;
          }
        }
      }
      if (handledAsFunctionType) {
        // advance past this let statement and continue outer loop
        expr = expr.substring(sem + 1).trim();
        continue;
      }

      // Map plain boolean literals to integers for C early
      if ("true".equals(initExpr)) {
        initExpr = "1";
      } else if ("false".equals(initExpr)) {
        initExpr = "0";
      }

      // Validate any identifiers referenced in the initializer are declared
      // (e.g. `let x = y;` where y must already be declared). Allow calls
      // to functions as well.
      validateIdentifiers(initExpr, declared, functions, types, functionParamTypes, callableVars, structFieldNames,
          structFieldTypes);

      ensureNotDeclared(name, declared);
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
            emitWrapperFunction(name, normalize(initExpr), new String[0], declared, functions, functionParamTypes,
                functionDefs);
            emittedWrapper = true;
          }
        }
      }
      if (!emittedWrapper) {
        addSimpleDeclaration(name, isMutable, declaredType, initExpr, declared, mutable, types, decls);
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
        // Support top-level struct declarations that may be followed by other
        // statements without an intervening semicolon, e.g.
        // "struct Point { x: I32 } let p = Point { readInt() };"
        if (stmt.startsWith("struct ")) {
          int nameStart = 7;
          int braceOpen = stmt.indexOf('{', nameStart);
          if (braceOpen < 0) {
            throw new CompileException("Malformed struct declaration: " + stmt);
          }
          // find matching '}'
          int close = findMatching(stmt, braceOpen, '{', '}');
          if (close < 0)
            throw new CompileException("Malformed struct declaration: " + stmt);
          String name = stmt.substring(nameStart, braceOpen).trim();
          if (name.isEmpty())
            throw new CompileException("Malformed struct declaration: " + stmt);
          String structBody = stmt.substring(braceOpen + 1, close).trim();
          // parse fields like "x: I32, y: I32"
          java.util.List<String> fields = new java.util.ArrayList<>();
          java.util.List<String> typesList = new java.util.ArrayList<>();
          if (!structBody.isEmpty()) {
            String[] parts = structBody.split(",");
            for (String p : parts) {
              String f = p.trim();
              int colon = f.indexOf(':');
              if (colon < 0)
                throw new CompileException("Malformed struct field: " + f);
              String fname = f.substring(0, colon).trim();
              String ftype = f.substring(colon + 1).trim();
              if (fname.isEmpty() || ftype.isEmpty())
                throw new CompileException("Malformed struct field: " + f);
              fields.add(fname);
              typesList.add(ftype);
            }
          }
          if (structFieldNames.containsKey(name))
            throw new CompileException("Duplicate struct: " + name);
          structFieldNames.put(name, fields);
          structFieldTypes.put(name, typesList);
          // emit a simple typedef mapping fields to `int` (I32/Bool -> int)
          structDefs.append("typedef struct {\n");
          for (int fi = 0; fi < fields.size(); fi++) {
            String ftype = typesList.get(fi);
            String ctype = ("I32".equals(ftype) || "Bool".equals(ftype)) ? "int" : ftype;
            structDefs.append("  ").append(ctype).append(" ").append(fields.get(fi)).append(";\n");
          }
          structDefs.append("} ").append(name).append(";\n\n");
          // if there's remaining code after the struct close in this stmt,
          // push it back into the remaining buffer to be processed as its own
          // statement(s).
          String rest = stmt.substring(close + 1).trim();
          String after = remaining.substring(idx + 1).trim();
          if (!rest.isEmpty()) {
            remaining = rest + ";" + after;
          } else {
            remaining = after;
          }
          if (remaining.isEmpty()) {
            lastSegment = "";
            break;
          }
          // continue processing without appending a body line for the struct
          continue;
        }
        // Support top-level blocks `{ ... }` that may be followed by other
        // statements without an intervening semicolon, e.g. "{} let x = 1;".
        if (stmt.startsWith("{")) {
          int closeB = findMatching(stmt, 0, '{', '}');
          if (closeB < 0)
            throw new CompileException("Malformed block: " + stmt);
          String restB = stmt.substring(closeB + 1).trim();
          String afterB = remaining.substring(idx + 1).trim();
          if (!restB.isEmpty()) {
            remaining = restB + ";" + afterB;
          } else {
            remaining = afterB;
          }
          if (remaining.isEmpty()) {
            lastSegment = "";
            break;
          }
          // continue processing the remainder (do not append a body line for
          // the block here)
          continue;
        }
        // validate assignment statements like: name = expr
        // Handle function declarations of the form: fn name() => expr
        if (stmt.startsWith("let ")) {
          // parse a single let declaration inside the body
          int eq = stmt.indexOf('=');
          if (!(eq > 0)) {
            throw new CompileException("Malformed let statement: " + stmt);
          }
          String namePart = stmt.substring(4, eq).trim();
          boolean isMutable = false;
          if (namePart.startsWith("mut ")) {
            isMutable = true;
            namePart = namePart.substring(4).trim();
          }
          int colonIdx = namePart.indexOf(':');
          String name = colonIdx >= 0 ? namePart.substring(0, colonIdx).trim() : namePart;
          String typeStr = colonIdx >= 0 ? namePart.substring(colonIdx + 1).trim() : "";
          String initExpr = stmt.substring(eq + 1).trim();
          // If there's a trailing semicolon accidentally included, remove it
          if (initExpr.endsWith(";"))
            initExpr = initExpr.substring(0, initExpr.length() - 1).trim();

          String declaredType;
          if (!typeStr.isEmpty()) {
            declaredType = typeStr;
            checkExplicitTypeCompatibility(typeStr, initExpr);
          } else {
            // detect struct constructor: TypeName { ... }
            Optional<String> st = detectStructTypeName(initExpr, structFieldNames);
            if (st.isPresent()) {
              declaredType = st.get();
            } else if ("true".equals(initExpr) || "false".equals(initExpr)) {
              declaredType = "Bool";
            } else {
              declaredType = "I32";
            }
          }

          // Map plain boolean literals to integers for C early
          if ("true".equals(initExpr))
            initExpr = "1";
          else if ("false".equals(initExpr))
            initExpr = "0";

          // Validate identifiers used in the initializer
          validateIdentifiers(initExpr, declared, functions, types, functionParamTypes, callableVars, structFieldNames,
              structFieldTypes);

          ensureNotDeclared(name, declared);

          // Handle struct construction initializer
          int braceIdx = initExpr.indexOf('{');
          boolean isStructInit = false;
          String cInit = null;
          if (braceIdx > 0 && initExpr.endsWith("}")) {
            Optional<String> st = detectStructTypeName(initExpr, structFieldNames);
            if (st.isPresent()) {
              String tname = st.get();
              isStructInit = true;
              String inner = initExpr.substring(braceIdx + 1, initExpr.length() - 1).trim();
              // Build inner C initializer, handling nested struct constructors
              String normInner = assembleStructInit(inner, structFieldNames);
              cInit = "{ " + normInner + " }";
              declared.add(name);
              types.put(name, tname);
              decls.append("  ").append(tname).append(" ").append(name).append(" = ").append(cInit).append(";\n");
              if (isMutable)
                mutable.add(name);
            }
          }

          if (!isStructInit) {
            addSimpleDeclaration(name, isMutable, declaredType, initExpr, declared, mutable, types, decls);
          }
          // done handling this stmt
          remaining = remaining.substring(idx + 1).trim();
          if (remaining.isEmpty()) {
            lastSegment = "";
            break;
          }
          continue;
        }
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
          validateIdentifiers(fbody, declaredWithParams, functions, types, functionParamTypes, new HashSet<String>(),
              structFieldNames, structFieldTypes);
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
          int assignIdx = findAssignEquals(stmt, 0);
          boolean skipBodyAppend = false;
          if (assignIdx > 0) {
            String lhs = stmt.substring(0, assignIdx).trim();
            String rhs = stmt.substring(assignIdx + 1).trim();
            // If rhs is a zero-arg lambda like '() => expr' and lhs is a
            // previously-declared mutable callable variable, emit a wrapper
            // function named lhs so subsequent calls resolve to a function.
            if (rhs.startsWith("() =>") && declared.contains(lhs) && mutable.contains(lhs)) {
              String lambdaBody = rhs.substring(5).trim();
              // validate the lambda body using current declarations
              validateIdentifiers(lambdaBody, declared, functions, types, functionParamTypes, callableVars,
                  structFieldNames, structFieldTypes);
              // If a placeholder variable was declared earlier for this callable,
              // remove that placeholder so we can register a real function.
              if (declared.contains(lhs) && callableVars.contains(lhs)) {
                declared.remove(lhs);
                mutable.remove(lhs);
                types.remove(lhs);
                callableVars.remove(lhs);
                // remove the placeholder declaration line if present
                String placeholder = "  int " + lhs + " = (0);\n";
                int p = decls.indexOf(placeholder);
                if (p >= 0) {
                  decls.delete(p, p + placeholder.length());
                }
              }
              if (functions.containsKey(lhs)) {
                throw new CompileException("Duplicate function or variable: " + lhs);
              }
              // register the wrapper function
              functions.put(lhs, 0);
              functionParamTypes.put(lhs, new String[0]);
              functionDefs.append("int ").append(lhs).append("() {\n");
              functionDefs.append("  return (").append(normalize(lambdaBody)).append(");\n");
              functionDefs.append("}\n\n");
              // don't emit the original 'f = () => 42;' line into body (invalid C)
              skipBodyAppend = true;
            } else {
              validateAssignment(lhs, rhs, declared, mutable, types, functions, functionParamTypes, structFieldNames,
                  structFieldTypes);
            }
          }
          // For non-assignment expression statements, ensure identifiers exist
          if (assignIdx < 0) {
            validateIdentifiers(stmt, declared, functions, types, functionParamTypes, callableVars, structFieldNames,
                structFieldTypes);
          }
          if (!skipBodyAppend) {
            body.append("  ").append(normalize(stmt)).append(";\n");
          }
        }
      }
      remaining = remaining.substring(idx + 1).trim();
      if (remaining.isEmpty()) {
        lastSegment = "";
        break;
      }
    }

    String finalExpr = (lastSegment == null || lastSegment.isEmpty()) ? "0" : lastSegment;
    // If the final expression is a block `{ ... }`, unwrap it. If the block
    // is empty treat it as 0. This avoids emitting C like `return ({...});`
    // which is invalid — instead we emit `return (<inner>);` or `return (0);`.
    String feTrimRaw = finalExpr.trim();
    if (feTrimRaw.startsWith("{") && feTrimRaw.endsWith("}")) {
      String inner = feTrimRaw.substring(1, feTrimRaw.length() - 1).trim();
      if (inner.isEmpty()) {
        finalExpr = "0";
      } else {
        finalExpr = inner;
      }
    }
    if ("true".equals(finalExpr)) {
      finalExpr = "1";
    } else if ("false".equals(finalExpr)) {
      finalExpr = "0";
    } else {
      // If final expression is an assignment, validate mutability too.
      int assignIdx = findAssignEquals(finalExpr, 0);
      if (assignIdx > 0) {
        String lhs = finalExpr.substring(0, assignIdx).trim();
        String rhs = finalExpr.substring(assignIdx + 1).trim();
        validateAssignment(lhs, rhs, declared, mutable, types, functions, functionParamTypes, structFieldNames,
            structFieldTypes);
      }
      // Validate identifiers used in the final expression as well.
      validateIdentifiers(finalExpr, declared, functions, types, functionParamTypes, callableVars, structFieldNames,
          structFieldTypes);
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
        structDefs.toString() +
        functionDefs.toString() +
        "int main(void) {\n" +
        decls.toString() +
        body.toString() +
        "  return (" + finalExpr + ");\n" +
        "}\n";
  }

  private static void validateAssignment(String lhs, String rhs, Set<String> declared, Set<String> mutable,
      Map<String, String> types, Map<String, Integer> functions, Map<String, String[]> functionParamTypes,
      Map<String, java.util.List<String>> structFieldNames, Map<String, java.util.List<String>> structFieldTypes) {
    if (!declared.contains(lhs)) {
      throw new CompileException("Assignment to undeclared variable: " + lhs);
    }
    if (!mutable.contains(lhs)) {
      throw new CompileException("Assignment to immutable variable: " + lhs);
    }
    // Ensure any identifiers referenced on the RHS are declared.
    validateIdentifiers(rhs, declared, functions, types, functionParamTypes, new HashSet<String>(), structFieldNames,
        structFieldTypes);
    String declaredType = types.get(lhs);
    if (declaredType != null) {
      checkExplicitTypeCompatibility(declaredType, rhs);
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
      if (isIdentStart(c)) {
        int j = scanIdentEnd(out, i);
        String tok = out.substring(i, j);
        if ("true".equals(tok)) {
          b.append('1');
        } else if ("false".equals(tok)) {
          b.append('0');
        } else if ("if".equals(tok)) {
          // Keep the 'if' token so rewriteIfElse can locate and transform
          // `if (cond) then else else` sequences into C ternary operators.
          b.append(tok);
        } else {
          b.append(tok);
        }
        i = j;
      } else {
        b.append(c);
        i++;
      }
    }
    return rewriteIfElse(b.toString());
  }

  private static String rewriteIfElse(String s) {
    if (s.indexOf("if") < 0)
      return s;
    String out = s;
    int maxLoops = 8;
    while (maxLoops-- > 0) {
      int n = out.length();
      int pos = findStandaloneToken(out, "if", 0);
      if (pos < 0)
        break;

      int i = pos + 2;
      i = skipWhitespace(out, i);
      int condStart = i;
      int condEnd = findCondEndAtDepthZero(out, i);
      i = condEnd;
      if (condEnd == condStart)
        condEnd = i;
      i = skipWhitespace(out, i);
      // Support two syntaxes:
      // 1) if (cond) thenExpr else elseExpr
      // 2) if (cond) ? thenExpr : elseExpr
      int thenStart = i;
      int thenEnd = -1;
      int elseStart = -1;
      int elseEnd = n;
      // Detect explicit '?' form first
      if (i < n && out.charAt(i) == '?') {
        // form: '? then : else'
        int qPos = i;
        int j = qPos + 1;
        // find ':' at depth zero - scan for matching parens to skip nested
        int colonPos = -1;
        for (; j < n; j++) {
          char c = out.charAt(j);
          if (c == '(') {
            int match = findMatching(out, j, '(', ')');
            if (match < 0)
              break;
            j = match;
            continue;
          }
          if (c == ':' && (j == qPos + 1 || out.charAt(j - 1) != '?')) {
            colonPos = j;
            break;
          }
        }
        if (colonPos < 0)
          break; // malformed ternary; give up
        thenStart = qPos + 1;
        thenEnd = colonPos;
        // else starts after ':'
        elseStart = colonPos + 1;
        // find end of else expression (up to next ';' or end or unmatched ')')
        int k = elseStart;
        for (; k < n; k++) {
          char c = out.charAt(k);
          if (c == '(') {
            int match = findMatching(out, k, '(', ')');
            if (match < 0)
              break;
            k = match;
            continue;
          }
          if (c == ')' || c == ';') {
            elseEnd = k;
            break;
          }
        }
      } else {
        // Traditional 'if cond then else' form: find 'else' token at depth 0
        int i0 = i;
        int elsePos = -1;
        for (; i0 < n; i0++) {
          char c = out.charAt(i0);
          if (c == '(') {
            int match = findMatching(out, i0, '(', ')');
            if (match < 0)
              break;
            i0 = match;
            continue;
          }
          if ((Character.isLetter(c) || c == '_')) {
            int j = scanIdentEnd(out, i0);
            String tok = out.substring(i0, j);
            if ("else".equals(tok)) {
              elsePos = i0;
              thenEnd = i0;
              i0 = j;
              break;
            }
          }
        }
        if (elsePos < 0)
          break;
        int ii = skipWhitespace(out, i0);
        elseStart = ii;
        // find end of else expression
        int endIdx = n;
        for (int ii2 = ii; ii2 < n; ii2++) {
          char c = out.charAt(ii2);
          if (c == '(') {
            int match = findMatching(out, ii2, '(', ')');
            if (match < 0) {
              endIdx = ii2;
              break;
            }
            ii2 = match;
            continue;
          }
          if (c == ')' || c == ';') {
            endIdx = ii2;
            break;
          }
        }
        elseEnd = endIdx;
        thenStart = i;
      }
      String pre = out.substring(0, pos);
      String cond = out.substring(condStart, condEnd).trim();
      String thenExpr = out.substring(thenStart, thenEnd).trim();
      String elseExpr = out.substring(elseStart, elseEnd).trim();
      String post = out.substring(elseEnd);
      out = pre + "(" + cond + ") ? " + thenExpr + " : " + elseExpr + post;
    }
    return out;
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

  private static void ensureCallableOrThrow(String t, Set<String> declared, Set<String> callableVars,
      Map<String, String> types) {
    if (declared.contains(t) && !callableVars.contains(t)) {
      String ttype = types.get(t);
      if (ttype == null || !ttype.contains("=>")) {
        throw new CompileException("Call of non-function identifier: " + t);
      }
    }
  }

  private static final class LetInfo {
    String name;
    boolean isMutable;
    String declaredType;
  }

  private static LetInfo parseLetHeader(String namePart, String initExpr,
      Map<String, java.util.List<String>> structFieldNames) {
    LetInfo info = new LetInfo();
    info.isMutable = false;
    String np = namePart;
    if (np.startsWith("mut ")) {
      info.isMutable = true;
      np = np.substring(4).trim();
    }
    int colonIdx = np.indexOf(':');
    info.name = colonIdx >= 0 ? np.substring(0, colonIdx).trim() : np;
    String typeStr = colonIdx >= 0 ? np.substring(colonIdx + 1).trim() : "";
    if (!typeStr.isEmpty()) {
      info.declaredType = typeStr;
    } else {
      Optional<String> st = detectStructTypeName(initExpr, structFieldNames);
      if (st.isPresent()) {
        info.declaredType = st.get();
      } else if ("true".equals(initExpr) || "false".equals(initExpr)) {
        info.declaredType = "Bool";
      } else {
        info.declaredType = "I32";
      }
    }
    return info;
  }

  // Centralized small helpers to reduce duplicated code
  private static void checkExplicitTypeCompatibility(String explicitType, String valueExpr) {
    if (explicitType == null || explicitType.isEmpty())
      return;
    if ("I32".equals(explicitType)) {
      if ("true".equals(valueExpr) || "false".equals(valueExpr)) {
        throw new CompileException("Type mismatch: cannot assign boolean to I32");
      }
    } else if ("Bool".equals(explicitType)) {
      if (!("true".equals(valueExpr) || "false".equals(valueExpr))) {
        throw new CompileException("Type mismatch: Bool must be assigned a boolean literal");
      }
    }
  }

  private static void ensureNotDeclared(String name, Set<String> declared) {
    if (declared.contains(name)) {
      throw new CompileException("Duplicate variable: " + name);
    }
  }

  private static void addSimpleDeclaration(String name, boolean isMutable, String declaredType, String initExpr,
      Set<String> declared, Set<String> mutable, Map<String, String> types, StringBuilder decls) {
    declared.add(name);
    if (isMutable)
      mutable.add(name);
    types.put(name, declaredType);
    decls.append("  int ").append(name).append(" = (").append(normalize(initExpr)).append(");\n");
  }

  private static Optional<String> detectStructTypeName(String initExpr,
      Map<String, java.util.List<String>> structFieldNames) {
    if (initExpr == null)
      return Optional.empty();
    int brace = initExpr.indexOf('{');
    if (brace > 0 && initExpr.endsWith("}")) {
      String tname = initExpr.substring(0, brace).trim();
      if (structFieldNames != null && structFieldNames.containsKey(tname)) {
        return Optional.of(tname);
      }
    }
    return Optional.empty();
  }

  // Generic matching helper to find the index of the matching closing
  // character (e.g. '}' for '{' or ')' for '(') starting at `start`.
  // Returns -1 if no matching closing char is found.
  private static int findMatching(String s, int start, char openChar, char closeChar) {
    int n = s.length();
    int depth = 0;
    for (int i = start; i < n; i++) {
      char c = s.charAt(i);
      if (c == openChar)
        depth++;
      else if (c == closeChar) {
        depth--;
        if (depth == 0)
          return i;
      }
    }
    return -1;
  }

  private static String assembleStructInit(String inner, Map<String, java.util.List<String>> structFieldNames) {
    java.util.List<String> parts = new java.util.ArrayList<>();
    int n = inner.length();
    int i = 0;
    StringBuilder cur = new StringBuilder();
    while (i < n) {
      char c = inner.charAt(i);
      if (c == '{') {
        int match = findMatching(inner, i, '{', '}');
        if (match < 0) {
          cur.append(c);
          i++;
          continue;
        }
        // append the whole nested brace section
        cur.append(inner, i, match + 1);
        i = match + 1;
        continue;
      }
      if (c == ',') {
        parts.add(cur.toString().trim());
        cur.setLength(0);
        i++;
        continue;
      }
      cur.append(c);
      i++;
    }
    if (cur.length() > 0)
      parts.add(cur.toString().trim());

    StringBuilder out = new StringBuilder();
    for (int pi = 0; pi < parts.size(); pi++) {
      String p = parts.get(pi);
      Optional<String> st = detectStructTypeName(p, structFieldNames);
      if (st.isPresent()) {
        int b = p.indexOf('{');
        String inner2 = p.substring(b + 1, p.length() - 1).trim();
        out.append("{ ").append(assembleStructInit(inner2, structFieldNames)).append(" }");
      } else {
        out.append(normalize(p));
      }
      if (pi + 1 < parts.size())
        out.append(", ");
    }
    return out.toString();
  }

  private static void validateIdentifiers(String expr, Set<String> declared, Map<String, Integer> functions,
      Map<String, String> types, Map<String, String[]> functionParamTypes, Set<String> callableVars,
      Map<String, java.util.List<String>> structFieldNames, Map<String, java.util.List<String>> structFieldTypes) {
    if (expr == null || expr.trim().isEmpty()) {
      return;
    }
    // Scan the string for identifier-like tokens (start with letter or '_', then
    // letters/digits/_). We also accept dotted field access like `p.x` where
    // the left side may be a struct variable and the right side a field name.
    int n = expr.length();
    StringBuilder token = new StringBuilder();
    for (int i = 0; i <= n; i++) {
      char c = i < n ? expr.charAt(i) : '\0';
      if (isIdentStart(c)) {
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

        // allow 'if'/'else' keywords in conditional expressions
        if ("if".equals(t) || "else".equals(t)) {
          token.setLength(0);
          continue;
        }

        // allow primitive type names to appear in annotations without treating
        // them as undefined identifiers when they accidentally end up in
        // expression fragments
        if ("I32".equals(t) || "Bool".equals(t)) {
          token.setLength(0);
          continue;
        }

        // accept struct type names as constructors (e.g. Point { ... })
        boolean isStructType = structFieldNames != null && structFieldNames.containsKey(t);

        // allowed identifiers: boolean literals and builtins
        if (!"true".equals(t) && !"false".equals(t) && !functions.containsKey(t) && !isStructType) {
          if (!declared.contains(t) && !functions.containsKey(t)) {
            throw new CompileException("Use of undefined identifier: " + t);
          }
        }

        // If this token is a call, prefer treating it as a function name when
        // possible; only if it's not a function and is a declared variable do
        // we report calling a non-function identifier.
        if (isCall) {
          // If a variable with this name has been declared it shadows any
          // function of the same name. Calling a non-callable variable is
          // a compile-time error unless it was explicitly annotated as a
          // function type or marked callable.
          if (declared.contains(t)) {
            ensureCallableOrThrow(t, declared, callableVars, types);
          } else if (functions.containsKey(t)) {
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
          } else {
            // Not a known function: if a variable exists with this name but
            // it's not marked callable, calling it is an error. However, if
            // the variable has an explicit function type annotation (e.g.
            // "() => I32") accept the call.
            ensureCallableOrThrow(t, declared, callableVars, types);
          }
        }

        // Support dotted field access 'p.x' and chained access 'p.x.y'. Walk the
        // chain and resolve each field's type using structFieldNames/types maps.
        if (i < n && expr.charAt(i) == '.') {
          int cur = i;
          String leftType = types.get(t);
          if (leftType == null) {
            throw new CompileException("Use of undefined identifier: " + t);
          }
          boolean consumed = false;
          while (cur < n && expr.charAt(cur) == '.') {
            int dotIdx = cur + 1;
            while (dotIdx < n && Character.isWhitespace(expr.charAt(dotIdx)))
              dotIdx++;
            if (dotIdx >= n || !isIdentStart(expr.charAt(dotIdx)))
              break;
            int end = scanIdentEnd(expr, dotIdx);
            String field = expr.substring(dotIdx, end);
            java.util.List<String> flds = structFieldNames.get(leftType);
            java.util.List<String> ftypes = structFieldTypes.get(leftType);
            if (flds == null || ftypes == null) {
              throw new CompileException("Struct " + leftType + " has no field " + field);
            }
            int idx = -1;
            for (int ii = 0; ii < flds.size(); ii++) {
              if (flds.get(ii).equals(field)) {
                idx = ii;
                break;
              }
            }
            if (idx < 0) {
              throw new CompileException("Struct " + leftType + " has no field " + field);
            }
            leftType = ftypes.get(idx);
            cur = end;
            consumed = true;
          }
          if (consumed) {
            i = cur;
            token.setLength(0);
            continue;
          }
        }

        token.setLength(0);
      }
      // otherwise skip this character
    }
  }
}

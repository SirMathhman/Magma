package org.example;

/** Minimal Intrepreter class. */
public class Intrepreter {
  // Simple per-run function registry using ThreadLocal so static helpers can
  // access it
  private static final ThreadLocal<java.util.Map<String, FunctionInfo>> FUNC_REG = new ThreadLocal<>();
  // Per-run struct registry: just track struct names declared to prevent
  // duplicates
  private static final ThreadLocal<java.util.Set<String>> STRUCT_REG = new ThreadLocal<>();
  // Per-run variable environment for simple values, to support block scoping
  private static final ThreadLocal<java.util.Map<String, String>> VAR_ENV = new ThreadLocal<>();

  private static final class FunctionInfo {
    final java.util.List<String> paramNames;
    final String bodyValue; // currently only stores parsed value's string

    FunctionInfo(java.util.List<String> paramNames, String bodyValue) {
      this.paramNames = paramNames;
      this.bodyValue = bodyValue;
    }
  }

  public String interpret(String input) throws InterpretingException {
    return internalEval(input, true);
  }

  // Core evaluator with optional registry reset; used to preserve env/registries
  // in blocks
  private static String internalEval(String input, boolean resetRegistries) throws InterpretingException {
    if (input == null || input.isEmpty()) {
      throw new InterpretingException("Undefined value", String.valueOf(input));
    }

    if (resetRegistries) {
      // Reset registries for this run
      FUNC_REG.set(new java.util.HashMap<>());
      STRUCT_REG.set(new java.util.HashSet<>());
      VAR_ENV.set(new java.util.HashMap<>());
    } else {
      // Ensure there is some env present when evaluating nested blocks
      if (VAR_ENV.get() == null) {
        VAR_ENV.set(new java.util.HashMap<>());
      }
    }

    // Prepare trimmed view and fast paths
    int start = skipSpaces(input, 0);
    int end = input.length() - 1;
    while (end >= start && isSpace(input.charAt(end)))
      end--;
    String trimmed = (start <= end) ? input.substring(start, end + 1) : "";

    // 1) Fast path: plain decimal integer (allow surrounding spaces)
    if (!trimmed.isEmpty() && isAllDigits(trimmed)) {
      return trimmed;
    }

    // 1a) Fast path: boolean literals (allow surrounding spaces)
    if (isBooleanLiteral(trimmed)) {
      return trimmed;
    }

    // 1b) Block: "{ ... }" => evaluate inner content as a program with inherited
    // env
    if (start < input.length() && end >= start && input.charAt(start) == '{' && input.charAt(end) == '}') {
      String inner = input.substring(start + 1, end);
      return evalInChildEnv(inner);
    }

    // 1c) Bare identifier program: look up from env
    if (!trimmed.isEmpty() && isIdentStart(trimmed.charAt(0))) {
      String maybeId = parseIdentifier(trimmed, 0);
      if (maybeId != null && maybeId.length() == trimmed.length()) {
        String val = VAR_ENV.get() != null ? VAR_ENV.get().get(maybeId) : null;
        if (val != null) {
          return val;
        }
        throw new InterpretingException("Undefined value", input);
      }
    }

    // 2) Minimal language:
    // - let [mut] <id> = <int>; <expr>
    // - let [mut] <id> = <int>; <id> = <int>; <expr>
    // Where <expr> is either <id> or <int>. If reassignment occurs, it must be
    // the same identifier and only allowed when declared with 'mut'.
    int i = 0;
    final int n = input.length();

    i = skipSpaces(input, i);

    if (startsWithWord(input, i, "let")) {
      i = consumeKeywordWithSpace(input, i, "let");

      boolean isMutable = false;
      // optional 'mut' followed by at least one space
      if (startsWithWord(input, i, "mut")) {
        i = consumeKeywordWithSpace(input, i, "mut");
        isMutable = true;
      }

      // identifier
      int idStart = i;
      String ident = parseIdentifier(input, i);
      if (ident == null) {
        throw new InterpretingException("Undefined value", input);
      }
      i = idStart + ident.length();

      // spaces and either '=' initializer or ':' typed declaration
      i = skipSpaces(input, i);
      if (i < n && input.charAt(i) == '=') {
        i++; // consume '='
        i = skipSpaces(input, i);

        // initializer: int | bool | block
        ValueParseResult init = parseValue(input, i);
        if (init == null) {
          throw new InterpretingException("Undefined value", input);
        }
        String intLit = init.value;
        i = init.nextIndex;
        // record into environment
        java.util.Map<String, String> env = VAR_ENV.get();
        updateEnvIfPresent(env, ident, intLit);

        // spaces
        i = skipSpaces(input, i);

        // ';' and spaces
        i = consumeSemicolonAndSpaces(input, i);

        // Next can be either an expression or a reassignment, then an expression.
        String currentVal = intLit;

        if (i < n && isIdentStart(input.charAt(i))) {
          String ref = parseIdentifier(input, i);
          if (ref == null) {
            throw new InterpretingException("Undefined value", input);
          }
          i += ref.length();
          int afterRef = skipSpaces(input, i);
          if (afterRef < n && input.charAt(afterRef) == '=') {
            // It's a reassignment statement: <id> = <int>;
            if (!ref.equals(ident)) {
              throw new InterpretingException("Mismatched assignment target: expected '" + ident + "'", ref);
            }
            if (!isMutable) {
              throw new InterpretingException("Cannot reassign immutable variable '" + ref + "'", input);
            }
            i = afterRef + 1; // consume '='
            i = skipSpaces(input, i);
            ValueParseResult re = parseValue(input, i);
            if (re == null) {
              throw new InterpretingException("Expected value after '=' in assignment to '" + ident + "'", input);
            }
            String reassigned = re.value;
            i = re.nextIndex;
            i = skipSpaces(input, i);
            i = consumeSemicolonAndSpaces(input, i);
            currentVal = reassigned;
            // update env
            updateEnvIfPresent(env, ident, reassigned);

            // After reassignment, we expect the final expression
            if (i >= n) {
              throw new InterpretingException("Undefined value", input);
            }
          } else {
            // Not an assignment; treat the identifier we already parsed as the expression
            if (!ref.equals(ident)) {
              throw new InterpretingException("Expected final identifier '" + ident + "'", ref);
            }
            // trailing spaces already in 'afterRef'
            i = afterRef;
            // trailing spaces
            i = skipSpaces(input, i);
            ensureNoTrailing(input, i);
            return currentVal;
          }
        }

        return finishWithExpressionOrValue(input, i, ident, currentVal, false);
      } else if (i < n && input.charAt(i) == ':') {
        // typed declaration: let <id> : <Type> ;
        i++; // consume ':'
        i = skipSpaces(input, i);
        String typeId = parseIdentifier(input, i);
        if (typeId == null) {
          throw new InterpretingException("Expected type identifier after ':'", input);
        }
        i += typeId.length();

        // spaces and ';'
        i = skipSpaces(input, i);
        i = consumeSemicolonAndSpaces(input, i);

        // After typed declaration, support either:
        // - if (cond) <id> = <value> else <id> = <value>; <expr>
        // - <id> = <value>; <expr>
        String currentVal = null;

        if (startsWithWord(input, i, "if")) {
          // if-statement with assignments
          int j = i;
          j = consumeKeywordWithSpace(input, j, "if");
          j = skipSpaces(input, j);
          j = expectCharOrThrow(input, j, '(');
          j = skipSpaces(input, j);
          ValueParseResult cond = parseBooleanConditionOrThrow(input, j);
          j = cond.nextIndex;
          j = skipSpaces(input, j);
          j = expectCharOrThrow(input, j, ')');
          j = skipSpaces(input, j);

          AssignmentParseResult thenAsg = parseAssignmentTo(input, j, ident);
          if (thenAsg == null) {
            throw new InterpretingException("Expected assignment to '" + ident + "' in then-branch", input);
          }
          j = thenAsg.nextIndex;
          j = skipSpaces(input, j);
          j = consumeKeywordWithSpace(input, j, "else");
          j = skipSpaces(input, j);
          AssignmentParseResult elseAsg = parseAssignmentTo(input, j, ident);
          if (elseAsg == null) {
            throw new InterpretingException("Expected assignment to '" + ident + "' in else-branch", input);
          }
          j = elseAsg.nextIndex;
          j = skipSpaces(input, j);
          j = consumeSemicolonAndSpaces(input, j);

          currentVal = "true".equals(cond.value) ? thenAsg.value : elseAsg.value;
          updateEnv(ident, currentVal);
          // assigned
          i = j; // advance
        } else {
          // direct assignment
          AssignmentParseResult asg = parseAssignmentTo(input, i, ident);
          if (asg != null) {
            i = asg.nextIndex;
            i = skipSpaces(input, i);
            i = consumeSemicolonAndSpaces(input, i);
            currentVal = asg.value;
            updateEnv(ident, currentVal);
            // assigned
          }
        }

        // Allow zero or more statements (block/while/for/fn/struct) before the final
        // expression
        i = skipSpaces(input, i);
        while (true) {
          int next = parseBlockStatement(input, i);
          if (next < 0 || next == i) {
            next = parseWhileStatement(input, i);
          }
          if (next < 0 || next == i) {
            next = parseForStatement(input, i);
            if (next < 0 || next == i) {
              next = parseFunctionDeclStatement(input, i);
              if (next < 0 || next == i) {
                next = parseStructDeclStatement(input, i);
              }
              if (next < 0 || next == i)
                break;
            }
          }
          i = next;
          i = skipSpaces(input, i);
        }

        return finishWithExpressionOrValue(input, i, ident, currentVal, true);
      } else {
        throw new InterpretingException("Undefined value", input);
      }
    }

    // Fallback: allow a standalone expression/value (supports arithmetic, booleans,
    // blocks, function calls)
    ValueParseResult expr = parseValue(input, i);
    if (expr != null) {
      int after = skipSpaces(input, expr.nextIndex);
      ensureNoTrailing(input, after);
      return expr.value;
    }

    // Anything else is currently undefined.
    throw new InterpretingException("Undefined value", input);
  }

  private static boolean isAllDigits(String s) {
    if (s.isEmpty())
      return false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c < '0' || c > '9')
        return false;
    }
    return true;
  }

  private static boolean startsWithWord(String s, int i, String word) {
    int n = s.length();
    int w = word.length();
    if (i + w - 1 >= n)
      return false;
    for (int k = 0; k < w; k++) {
      if (s.charAt(i + k) != word.charAt(k))
        return false;
    }
    return true;
  }

  private static boolean isSpace(char c) {
    return c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f';
  }

  private static int skipSpaces(String s, int i) {
    final int n = s.length();
    while (i < n && isSpace(s.charAt(i)))
      i++;
    return i;
  }

  private static int consumeKeywordWithSpace(String s, int i, String word) {
    if (!startsWithWord(s, i, word)) {
      throw new InterpretingException("Expected keyword '" + word + "'", s);
    }
    i += word.length();
    if (i >= s.length() || !isSpace(s.charAt(i))) {
      throw new InterpretingException("Expected space after keyword '" + word + "'", s);
    }
    return skipSpaces(s, i);
  }

  private static boolean isIdentStart(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
  }

  private static boolean isIdentPart(char c) {
    return isIdentStart(c) || (c >= '0' && c <= '9');
  }

  private static boolean isBooleanLiteral(String s) {
    return "true".equals(s) || "false".equals(s);
  }

  // Evaluate input string using a child environment that inherits current VAR_ENV
  private static String evalInChildEnv(String input) {
    // Snapshot current environments/registries
    java.util.Map<String, String> prevVar = VAR_ENV.get();
    java.util.Map<String, FunctionInfo> prevFunc = FUNC_REG.get();
    java.util.Set<String> prevStruct = STRUCT_REG.get();

    // Create child views inheriting from parents
    java.util.Map<String, String> parentVar = (prevVar == null) ? new java.util.HashMap<>() : prevVar;
    java.util.Map<String, FunctionInfo> parentFunc = (prevFunc == null) ? new java.util.HashMap<>() : prevFunc;
    java.util.Set<String> parentStruct = (prevStruct == null) ? new java.util.HashSet<>() : prevStruct;

    java.util.Map<String, String> childVar = new java.util.HashMap<>(parentVar);
    java.util.Map<String, FunctionInfo> childFunc = new java.util.HashMap<>(parentFunc);
    java.util.Set<String> childStruct = new java.util.HashSet<>(parentStruct);

    VAR_ENV.set(childVar);
    FUNC_REG.set(childFunc);
    STRUCT_REG.set(childStruct);
    try {
      return internalEval(input, false);
    } finally {
      // Restore previous environments/registries
      VAR_ENV.set(prevVar);
      FUNC_REG.set(prevFunc);
      STRUCT_REG.set(prevStruct);
    }
  }

  private static final class ValueParseResult {
    final String value;
    final int nextIndex;

    ValueParseResult(String value, int nextIndex) {
      this.value = value;
      this.nextIndex = nextIndex;
    }
  }

  private static final class AssignmentParseResult {
    final String value;
    final int nextIndex;

    AssignmentParseResult(String value, int nextIndex) {
      this.value = value;
      this.nextIndex = nextIndex;
    }
  }

  // Primary (non-infix) values: if/else, match, block, boolean, function call,
  // integer
  private static ValueParseResult parsePrimaryValue(String s, int i) {
    final int n = s.length();
    if (i >= n)
      return null;
    i = skipSpaces(s, i);

    // if (cond) then else
    if (startsWithWord(s, i, "if")) {
      i = consumeKeywordWithSpace(s, i, "if");
      i = skipSpaces(s, i);
      i = expectCharOrThrow(s, i, '(');
      i = skipSpaces(s, i);
      ValueParseResult cond = parseValue(s, i);
      if (!isBooleanResult(cond)) {
        return null;
      }
      i = cond.nextIndex;
      i = skipSpaces(s, i);
      i = expectCharOrThrow(s, i, ')');
      i = skipSpaces(s, i);
      ValueParseResult thenV = parseValue(s, i);
      if (thenV == null)
        return null;
      i = thenV.nextIndex;
      i = skipSpaces(s, i);
      i = consumeKeywordWithSpace(s, i, "else");
      i = skipSpaces(s, i);
      ValueParseResult elseV = parseValue(s, i);
      if (elseV == null)
        return null;
      i = elseV.nextIndex;
      String picked = "true".equals(cond.value) ? thenV.value : elseV.value;
      return new ValueParseResult(picked, i);
    }
    // match expr
    if (startsWithWord(s, i, "match")) {
      return parseMatchExpression(s, i);
    }
    // block
    if (s.charAt(i) == '{') {
      int close = findMatchingBrace(s, i);
      if (close < 0)
        return null;
      String inner = s.substring(i + 1, close);
      String val = evalInChildEnv(inner);
      return new ValueParseResult(val, close + 1);
    }
    // boolean
    if (startsWithWord(s, i, "true")) {
      return new ValueParseResult("true", i + 4);
    }
    if (startsWithWord(s, i, "false")) {
      return new ValueParseResult("false", i + 5);
    }
    // function call: <ident> ( [value [, ...]] )
    if (isIdentStart(s.charAt(i))) {
      int idStart = i;
      String ident = parseIdentifier(s, i);
      if (ident != null) {
        i = idStart + ident.length();
        i = skipSpaces(s, i);
        if (i < n && s.charAt(i) == '(') {
          // ensure not a reserved keyword like 'if' or 'match'
          if (!"if".equals(ident) && !"match".equals(ident)) {
            ValueParseResult call = parseFunctionCallExpression(s, idStart);
            return call;
          }
        }
      }
      // otherwise, fall through; bare identifiers are not values here
      i = idStart; // restore to avoid partial consumption
    }
    // integer
    String intLit = parseInteger(s, i);
    if (intLit != null) {
      return new ValueParseResult(intLit, i + intLit.length());
    }
    return null;
  }

  private static boolean isIntVal(String v) {
    return v != null && isAllDigits(v);
  }

  private static int parseIntStrict(String v, String s) {
    if (!isIntVal(v))
      throw new InterpretingException("Undefined value", s);
    return Integer.parseInt(v);
  }

  private static ValueParseResult applyBinOp(ValueParseResult left, ValueParseResult right, char op, String s) {
    int a = parseIntStrict(left.value, s);
    int b = parseIntStrict(right.value, s);
    int c;
    if (op == '*') {
      c = a * b;
    } else if (op == '+') {
      c = a + b;
    } else if (op == '-') {
      c = a - b;
    } else {
      throw new InterpretingException("Undefined value", s);
    }
    return new ValueParseResult(String.valueOf(c), right.nextIndex);
  }

  private static boolean isBoolString(String v) {
    return "true".equals(v) || "false".equals(v);
  }

  private static boolean parseBoolStrict(String v, String s) {
    if (!isBoolString(v))
      throw new InterpretingException("Undefined value", s);
    return Boolean.parseBoolean(v);
  }

  // Minimal functional interface to avoid java.util.function in token count
  private interface NextParser {
    ValueParseResult parse(String s, int i);
  }

  private static final class OpHit { int nextPos; char ch; OpHit(int n, char c){this.nextPos=n; this.ch=c;} }
  private interface OpDetector { OpHit detect(String s, int pos); }
  private interface Combiner { ValueParseResult combine(ValueParseResult left, ValueParseResult right, char op, String s); }

  // Generic left-associative chain parser with pluggable operator detection and combination
  private static ValueParseResult parseChain(String s, int i, NextParser next, OpDetector det, Combiner comb) {
    ValueParseResult left = next.parse(s, i);
    if (left == null)
      return null;
    int pos = skipSpaces(s, left.nextIndex);
    for (OpHit hit; (hit = det.detect(s, pos)) != null; ) {
      pos = skipSpaces(s, hit.nextPos);
      ValueParseResult right = next.parse(s, pos);
      if (right == null)
        throw new InterpretingException("Undefined value", s);
      left = comb.combine(left, right, hit.ch, s);
      pos = skipSpaces(s, left.nextIndex);
    }
    return left;
  }

  private static OpDetector fixedStringOp(String text, char tag) {
    return (str, p) -> (p + text.length() <= str.length() && str.startsWith(text, p)) ? new OpHit(p + text.length(), tag) : null;
  }

  private static final Combiner BOOL_COMBINER = (l, r, op, src) -> {
    boolean a = parseBoolStrict(l.value, src);
    boolean b = parseBoolStrict(r.value, src);
    boolean c = (op == '&') ? (a && b) : (a || b);
    return new ValueParseResult(c ? "true" : "false", r.nextIndex);
  };

  // logicalAnd := addSub ( '&&' addSub )*
  private static ValueParseResult parseLogicalAnd(String s, int i) {
  OpDetector andDetector = fixedStringOp("&&", '&');
  return parseChain(s, i, Intrepreter::parseExprAddSub, andDetector, BOOL_COMBINER);
  }

  // logicalOr := logicalAnd ( '||' logicalAnd )*
  private static ValueParseResult parseLogicalOr(String s, int i) {
  OpDetector orDetector = fixedStringOp("||", '|');
  return parseChain(s, i, Intrepreter::parseLogicalAnd, orDetector, BOOL_COMBINER);
  }
  // Generic left-associative infix parser over a set of operator characters
  private static ValueParseResult parseInfix(String s, int i, NextParser next, char... ops) {
    java.util.HashSet<Character> allowed = new java.util.HashSet<>();
    for (char c : ops) allowed.add(c);
    OpDetector det = (str, p) -> (p < str.length() && allowed.contains(str.charAt(p))) ? new OpHit(p + 1, str.charAt(p)) : null;
    Combiner comb = (l, r, op, src) -> applyBinOp(l, r, op, src);
    return parseChain(s, i, next, det, comb);
  }

  // term := primary ( '*' primary )*
  private static ValueParseResult parseTerm(String s, int i) {
    return parseInfix(s, i, Intrepreter::parsePrimaryValue, '*');
  }

  // expr := term ( ('+'|'-') term )*
  private static ValueParseResult parseExprAddSub(String s, int i) {
    return parseInfix(s, i, Intrepreter::parseTerm, '+', '-');
  }

  // Value now includes logical operators (||, &&) and arithmetic (+, -, *)
  private static ValueParseResult parseValue(String s, int i) {
    return parseLogicalOr(s, i);
  }

  private static String parseIdentifier(String s, int i) {
    int n = s.length();
    if (i >= n || !isIdentStart(s.charAt(i)))
      return null;
    int j = i + 1;
    while (j < n && isIdentPart(s.charAt(j)))
      j++;
    return s.substring(i, j);
  }

  // Parses: match <value> { <int> => <value> ; ... ; _ => <value> ; }
  // Returns the selected arm's value. Requires braces and semicolons between
  // arms.
  private static ValueParseResult parseMatchExpression(String s, int i) {
    i = consumeKeywordWithSpace(s, i, "match");
    i = skipSpaces(s, i);
    ValueParseResult subject = parseValue(s, i);
    if (subject == null) {
      throw new InterpretingException("Undefined value", s);
    }
    String subj = subject.value;
    i = subject.nextIndex;
    i = skipSpaces(s, i);
    i = expectCharOrThrow(s, i, '{');

    int pos = i + 1;
    String selected = null;
    boolean matched = false;
    boolean sawWildcard = false;
    String wildcardValue = null;

    while (true) {
      pos = skipSpaces(s, pos);
      if (pos >= s.length()) {
        throw new InterpretingException("Undefined value", s);
      }
      if (s.charAt(pos) == '}') {
        pos++; // end of match arms
        break;
      }

      // pattern: integer literal or '_'
      boolean isWildcard = false;
      String pat = null;
      if (s.charAt(pos) == '_') {
        isWildcard = true;
        pos++;
      } else {
        String lit = parseInteger(s, pos);
        if (lit == null) {
          throw new InterpretingException("Undefined value", s);
        }
        pat = lit;
        pos += lit.length();
      }

      ValueParseResult armVal = consumeArrowAndParseValue(s, pos);
      pos = armVal.nextIndex;
      pos = skipSpaces(s, pos);
      pos = expectCharOrThrow(s, pos, ';');
      pos = skipSpaces(s, pos);

      if (isWildcard) {
        sawWildcard = true;
        wildcardValue = armVal.value;
      } else if (!matched && pat.equals(subj)) {
        matched = true;
        selected = armVal.value;
      }
    }

    if (!matched) {
      if (sawWildcard) {
        selected = wildcardValue;
      } else {
        throw new InterpretingException("Undefined value", s);
      }
    }
    // pos points after '}'
    return new ValueParseResult(selected, pos);
  }

  private static String parseInteger(String s, int i) {
    int n = s.length();
    if (i >= n)
      return null;
    int j = i;
    while (j < n) {
      char c = s.charAt(j);
      if (c < '0' || c > '9')
        break;
      j++;
    }
    if (j == i)
      return null; // no digits
    return s.substring(i, j);
  }

  // Parses `<ident> = <value>` at position i, only if the identifier matches
  // expectedIdent.
  private static AssignmentParseResult parseAssignmentTo(String s, int i, String expectedIdent) {
    int n = s.length();
    int pos = skipSpaces(s, i);
    String id = parseIdentifier(s, pos);
    if (id == null || !id.equals(expectedIdent))
      return null;
    pos += id.length();
    pos = skipSpaces(s, pos);
    if (pos >= n || s.charAt(pos) != '=')
      return null;
    pos++;
    pos = skipSpaces(s, pos);
    ValueParseResult v = parseValue(s, pos);
    if (v == null)
      return null;
    return new AssignmentParseResult(v.value, v.nextIndex);
  }

  private static int expectCharOrThrow(String input, int i, char expected) {
    if (i >= input.length() || input.charAt(i) != expected) {
      throw new InterpretingException("Expected '" + expected + "'", input);
    }
    return i + 1;
  }

  // Expect the given char at pos, advance past it and skip following spaces.
  private static int expectAndSkip(String s, int pos, char expected) {
    pos = expectCharOrThrow(s, pos, expected);
    return skipSpaces(s, pos);
  }

  // Expect a two-character sequence (like '=>' ), advance after them and skip
  // spaces.
  private static int expectSequenceAndSkip(String s, int pos, char first, char second) {
    pos = expectCharOrThrow(s, pos, first);
    pos = expectCharOrThrow(s, pos, second);
    return skipSpaces(s, pos);
  }

  private static ValueParseResult parseBooleanConditionOrThrow(String s, int i) {
    ValueParseResult cond = parseValue(s, i);
    if (cond == null || !("true".equals(cond.value) || "false".equals(cond.value))) {
      throw new InterpretingException("Condition must be boolean", s);
    }
    return cond;
  }

  // Helper: update variable value in current environment
  private static void updateEnv(String name, String value) {
    java.util.Map<String, String> env = VAR_ENV.get();
    if (env != null)
      env.put(name, value);
  }

  // Helper used when calling sites already have local name/value variables and
  // need to update the env if present.
  private static void updateEnvIfPresent(java.util.Map<String, String> env, String name, String value) {
    if (env != null)
      env.put(name, value);
  }

  private static boolean isBooleanResult(ValueParseResult v) {
    return v != null && ("true".equals(v.value) || "false".equals(v.value));
  }

  private static int consumeSemicolonAndSpaces(String s, int i) {
    i = expectCharOrThrow(s, i, ';');
    return skipSpaces(s, i);
  }

  // Helper: parse a value or throw a uniform error
  private static ValueParseResult requireValue(String s, int pos) {
    ValueParseResult v = parseValue(s, pos);
    if (v == null) {
      throw new InterpretingException("Expected value", s);
    }
    return v;
  }

  // Helper: if there's a comma at pos (after skipping spaces), consume it and
  // following spaces.
  // Returns the advanced index; if no comma, returns the original or
  // spaces-skipped index.
  private static int consumeCommaAndSpaces(String s, int pos) {
    int start = pos;
    pos = skipSpaces(s, pos);
    if (pos < s.length()) {
      char ch = s.charAt(pos);
      if (ch == ',') {
        pos = pos + 1;
        return skipSpaces(s, pos);
      }
    }
    return start;
  }

  private static void ensureNoTrailing(String s, int i) {
    if (i != s.length()) {
      throw new InterpretingException("Unexpected trailing input", s.substring(i));
    }
  }

  private static String finishWithExpressionOrValue(String input, int i, String ident, String currentVal,
      boolean requireAssigned) {
    final int n = input.length();
    String result;
    if (i < n && isIdentStart(input.charAt(i))) {
      // Peek: if this is a function call (identifier followed by '('), parse as
      // value.
      int idStart = i;
      String id = parseIdentifier(input, i);
      int afterId = idStart + (id != null ? id.length() : 0);
      afterId = skipSpaces(input, afterId);
      if (id != null && afterId < n && input.charAt(afterId) == '(') {
        ValueParseResult v = requireValue(input, i);
        i = v.nextIndex;
        result = v.value;
      } else {
        String ref2 = parseIdentifier(input, i);
        if (ref2 == null) {
          throw new InterpretingException("Expected identifier or expression", input);
        }
        i += ref2.length();
        if (!ref2.equals(ident)) {
          throw new InterpretingException("Expected final identifier '" + ident + "'", ref2);
        }
        if (requireAssigned && currentVal == null) {
          throw new InterpretingException("Variable '" + ident + "' used before assignment", input);
        }
        result = currentVal;
      }
    } else {
      ValueParseResult v = requireValue(input, i);
      i = v.nextIndex;
      result = v.value;
    }

    // trailing spaces
    i = skipSpaces(input, i);
    ensureNoTrailing(input, i);
    return result;
  }

  private static int findMatchingBrace(String s, int openIndex) {
    if (openIndex < 0 || openIndex >= s.length() || s.charAt(openIndex) != '{')
      return -1;
    int depth = 0;
    for (int j = openIndex; j < s.length(); j++) {
      char c = s.charAt(j);
      if (c == '{')
        depth++;
      else if (c == '}') {
        depth--;
        if (depth == 0)
          return j;
      }
    }
    return -1; // no match
  }

  // Parses and consumes a required block: { body } ;? and returns next index
  private static int parseRequiredBlockAndOptionalSemicolon(String s, int pos) {
    int n = s.length();
    if (pos >= n || s.charAt(pos) != '{') {
      throw new InterpretingException("Undefined value", s);
    }
    int close = findMatchingBrace(s, pos);
    if (close < 0) {
      throw new InterpretingException("Undefined value", s);
    }
    pos = close + 1;
    pos = skipSpaces(s, pos);
    // optional semicolon after block for statement separation
    if (pos < n && s.charAt(pos) == ';') {
      pos++;
      pos = skipSpaces(s, pos);
    }
    return skipSpaces(s, pos);
  }

  // Parses a standalone block statement: { ... } ;? and returns next index or -1
  // if not present
  private static int parseBlockStatement(String s, int i) {
    int pos = skipSpaces(s, i);
    if (pos >= s.length() || s.charAt(pos) != '{')
      return -1;
    int close = findMatchingBrace(s, pos);
    if (close < 0)
      return -1;
    int after = skipSpaces(s, close + 1);
    // If there's a semicolon, always a statement
    if (after < s.length() && s.charAt(after) == ';') {
      return skipSpaces(s, after + 1);
    }
    // No semicolon: if there's more input after the block, treat as a statement;
    // if it's end-of-input, let caller treat it as an expression
    if (after < s.length()) {
      return after;
    }
    return -1;
  }

  // Consumes a boolean condition inside parentheses and returns its boolean value
  // and
  // the index after the closing ')', with spaces skipped.
  private static ValueParseResult consumeParenBooleanCondition(String s, int pos) {
    pos = expectOpenParenAndSkip(s, pos);
    ValueParseResult cond = parseBooleanConditionOrThrow(s, pos);
    pos = cond.nextIndex;
    pos = skipSpaces(s, pos);
    pos = expectCloseParenAndSkip(s, pos);
    return new ValueParseResult(cond.value, pos);
  }

  // Parses a while statement: while (cond) { body } ;? Returns next index or -1
  // if not present.
  private static int parseWhileStatement(String s, int i) {
    int pos = startKeywordPos(s, i, "while", true);
    if (pos < 0)
      return -1;
    pos = consumeKeywordWithSpace(s, pos, "while");
    pos = skipSpaces(s, pos);
    ValueParseResult condR = consumeParenBooleanCondition(s, pos);
    pos = condR.nextIndex;
    return parseRequiredBlockAndOptionalSemicolon(s, pos);
  }

  // Parses a for statement: for ( init ; cond ; incr ) { body } ;?
  // init: either "let [mut] <id> = <value>" or "<id> = <value>"
  // cond: boolean expression (must evaluate to true/false)
  // incr: "<id> = <value>"
  // Returns next index or -1 if not present.
  private static int parseForStatement(String s, int i) {
    int pos = startKeywordPos(s, i, "for", true);
    if (pos < 0)
      return -1;
    pos = consumeKeywordWithSpace(s, pos, "for");
    pos = skipSpaces(s, pos);
    pos = expectOpenParenAndSkip(s, pos);

    // init
    if (startsWithWord(s, pos, "let")) {
      pos = consumeKeywordWithSpace(s, pos, "let");
      pos = skipSpaces(s, pos);
      if (startsWithWord(s, pos, "mut")) {
        pos = consumeKeywordWithSpace(s, pos, "mut");
        pos = skipSpaces(s, pos);
      }
      String id = parseIdentifier(s, pos);
      if (id == null)
        throw new InterpretingException("Expected identifier after 'let'", s);
      pos += id.length();
      pos = parseAssignmentAfterKnownIdentifier(s, pos);
    } else {
      // assignment form: <id> = <value>
      int aPos = pos;
      String id = parseIdentifier(s, aPos);
      if (id == null)
        throw new InterpretingException("Expected identifier in for-init assignment", s);
      aPos += id.length();
      pos = parseAssignmentAfterKnownIdentifier(s, aPos);
    }

    pos = skipSpaces(s, pos);
    pos = expectAndSkip(s, pos, ';');

    // condition
    ValueParseResult cond = parseBooleanConditionOrThrow(s, pos);
    pos = skipSpaces(s, cond.nextIndex);
    pos = expectAndSkip(s, pos, ';');

    // increment: <id> = <value>
    String incId = parseIdentifier(s, pos);
    if (incId == null)
      throw new InterpretingException("Undefined value", s);
    pos = parseAssignmentAfterKnownIdentifier(s, pos + incId.length());

    pos = expectCloseParenAndSkip(s, pos);
    return parseRequiredBlockAndOptionalSemicolon(s, pos);
  }

  private static boolean hasKeywordBoundary(String s, int pos, int keywordLen) {
    int after = pos + keywordLen;
    return !(after < s.length() && isIdentPart(s.charAt(after)));
  }

  // Skip spaces and verify the upcoming word matches 'word'. If requireBoundary
  // is
  // true, ensure the word is not a prefix of a longer identifier. Returns the
  // position after skipping spaces (where the word starts) or -1 if it doesn't
  // match.
  private static int startKeywordPos(String s, int i, String word, boolean requireBoundary) {
    int pos = skipSpaces(s, i);
    if (!startsWithWord(s, pos, word))
      return -1;
    if (requireBoundary && !hasKeywordBoundary(s, pos, word.length()))
      return -1;
    return pos;
  }

  private static boolean isNextCharAfterSkip(String s, int pos, char ch) {
    pos = skipSpaces(s, pos);
    return (pos < s.length() && s.charAt(pos) == ch);
  }

  // After a parameter list has ended at pos, expect ')' ':' returnType and
  // return the parsed return type and next position via a small holder.
  private static java.util.Map.Entry<String, Integer> parseReturnTypeAfterParamList(String s, int pos) {
    pos = expectAndSkip(s, pos, ')');
    pos = expectAndSkip(s, pos, ':');
    String retType = parseIdentifier(s, pos);
    if (retType == null) {
      throw new InterpretingException("Expected return type after ':'", s);
    }
    pos += retType.length();
    pos = skipSpaces(s, pos);
    return new java.util.AbstractMap.SimpleEntry<>(retType, pos);
  }

  private static int expectOpenParenAndSkip(String s, int pos) {
    return expectAndSkip(s, pos, '(');
  }

  private static int expectCloseParenAndSkip(String s, int pos) {
    return expectAndSkip(s, pos, ')');
  }

  // Parses a function declaration statement:
  // fn <id> ( [<id> : <Type> [, ...]] ) : <Type> => <value> ;
  // Returns next index or -1 if not present.
  private static int parseFunctionDeclStatement(String s, int i) {
    int pos = startKeywordPos(s, i, "fn", true);
    if (pos < 0)
      return -1;
    // consume keyword and function name, and capture name text for registry
    int nameStartPos = consumeKeywordWithSpace(s, pos, "fn");
    nameStartPos = skipSpaces(s, nameStartPos);
    String fnName = parseIdentifier(s, nameStartPos);
    if (fnName == null)
      throw new InterpretingException("Expected function name after 'fn'", s);
    pos = nameStartPos + fnName.length();
    pos = skipSpaces(s, pos);
    pos = expectCharOrThrow(s, pos, '(');
    java.util.ArrayList<String> paramNames = new java.util.ArrayList<>();
    pos = parseOptionalNameTypeList(s, pos, ')', paramNames);
    // ensure no duplicate parameter names
    java.util.HashSet<String> seen = new java.util.HashSet<>();
    for (String p : paramNames) {
      if (!seen.add(p)) {
        throw new InterpretingException("Duplicate parameter name '" + p + "' in function '" + fnName + "'", s);
      }
    }
    java.util.Map.Entry<String, Integer> ret = parseReturnTypeAfterParamList(s, pos);
    pos = ret.getValue();
    ValueParseResult body = consumeArrowAndParseValue(s, pos);
    pos = skipSpaces(s, body.nextIndex);
    pos = expectAndSkip(s, pos, ';');
    // register function; error on duplicate name
    java.util.Map<String, FunctionInfo> reg = FUNC_REG.get();
    if (reg.containsKey(fnName)) {
      throw new InterpretingException("Function '" + fnName + "' already defined", s);
    }
    reg.put(fnName, new FunctionInfo(paramNames, body.value));
    return skipSpaces(s, pos);
  }

  // Consumes '=>' followed by a value; returns the parsed value and next index.
  private static ValueParseResult consumeArrowAndParseValue(String s, int pos) {
    pos = skipSpaces(s, pos);
    pos = expectSequenceAndSkip(s, pos, '=', '>');
    ValueParseResult v = requireValue(s, pos);
    return v;
  }

  // Overload with optional outNames: collects the names if provided.
  private static int parseOptionalNameTypeList(String s, int pos, char terminator, java.util.List<String> outNames) {
    pos = skipSpaces(s, pos);
    // empty list
    if (pos < s.length() && s.charAt(pos) == terminator) {
      return pos;
    }
    // non-empty list
    while (pos < s.length()) {
      String n = parseIdentifier(s, pos);
      if (n == null)
        throw new InterpretingException("Expected identifier", s);
      if (outNames != null)
        outNames.add(n);
      pos += n.length();
      pos = skipSpaces(s, pos);
      pos = expectCharOrThrow(s, pos, ':');
      pos = skipSpaces(s, pos);
      String t = parseIdentifier(s, pos);
      if (t == null)
        throw new InterpretingException("Expected type identifier after ':'", s);
      pos += t.length();
      int next = consumeCommaAndSpaces(s, pos);
      if (next != pos) {
        pos = next;
        // require another pair after comma; a terminator here means trailing comma =>
        // invalid
        if (isNextCharAfterSkip(s, pos, terminator)) {
          throw new InterpretingException("Trailing comma not allowed", s);
        }
        continue;
      }
      break;
    }
    return skipSpaces(s, pos);
  }

  // (removed helper skipCommaAndSpaces)

  // Parses a struct declaration statement:
  // struct <id> { [<field> : <Type> [, ...]] } ;
  // Returns next index or -1 if not present.
  private static int parseStructDeclStatement(String s, int i) {
    int pos = startKeywordPos(s, i, "struct", true);
    if (pos < 0)
      return -1;
    // consume 'struct' and capture the struct name for uniqueness checks
    pos = consumeKeywordWithSpace(s, pos, "struct");
    pos = skipSpaces(s, pos);
    String structName = parseIdentifier(s, pos);
    if (structName == null) {
      throw new InterpretingException("Expected struct name after 'struct'", s);
    }
    pos += structName.length();
    pos = skipSpaces(s, pos);
    // enforce unique struct names per run
    java.util.Set<String> sreg = STRUCT_REG.get();
    if (sreg.contains(structName)) {
      throw new InterpretingException("Struct '" + structName + "' already defined", s);
    }
    sreg.add(structName);
    pos = expectCharOrThrow(s, pos, '{');
    java.util.ArrayList<String> fieldNames = new java.util.ArrayList<>();
    pos = parseOptionalNameTypeList(s, pos, '}', fieldNames);
    // check for duplicate field names
    java.util.HashSet<String> seen = new java.util.HashSet<>();
    for (String f : fieldNames) {
      if (!seen.add(f)) {
        throw new InterpretingException("Duplicate field name '" + f + "' in struct '" + structName + "'", s);
      }
    }
    pos = expectCharOrThrow(s, pos, '}');
    pos = skipSpaces(s, pos);
    pos = expectAndSkip(s, pos, ';');
    return pos;
  }

  // After an identifier has been consumed, parse an assignment '=' <value> and
  // return next index
  private static int parseAssignmentAfterKnownIdentifier(String s, int pos) {
    pos = skipSpaces(s, pos);
    pos = expectCharOrThrow(s, pos, '=');
    pos = skipSpaces(s, pos);
    ValueParseResult v = parseValue(s, pos);
    if (v == null)
      throw new InterpretingException("Undefined value", s);
    return v.nextIndex;
  }

  // Parses a function call expression starting at the identifier of the name.
  // Returns value of the function body (currently ignoring params) and index
  // after ')'.
  private static ValueParseResult parseFunctionCallExpression(String s, int identStart) {
    String name = parseIdentifier(s, identStart);
    if (name == null) {
      throw new InterpretingException("Expected function name", s);
    }
    int pos = identStart + name.length();
    pos = skipSpaces(s, pos);
    pos = expectOpenParenAndSkip(s, pos);
    int argc = 0;
    if (pos < s.length() && s.charAt(pos) != ')') {
      while (true) {
        ValueParseResult arg = requireValue(s, pos);
        argc++;
        int next = consumeCommaAndSpaces(s, arg.nextIndex);
        if (next != arg.nextIndex) {
          pos = next;
          continue;
        }
        pos = arg.nextIndex;
        break;
      }
    }
    pos = expectCloseParenAndSkip(s, pos);
    java.util.Map<String, FunctionInfo> reg = FUNC_REG.get();
    FunctionInfo fi = (reg != null) ? reg.get(name) : null;
    if (fi == null) {
      throw new InterpretingException("Undefined function '" + name + "'", s);
    }
    if (fi.paramNames.size() != argc) {
      throw new InterpretingException(
          "Wrong number of arguments for '" + name + "' (expected " + fi.paramNames.size() + ", got " + argc + ")",
          s);
    }
    return new ValueParseResult(fi.bodyValue, pos);
  }
}

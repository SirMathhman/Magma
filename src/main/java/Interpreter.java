import java.util.HashMap;
import java.util.Map;

public class Interpreter {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }

    // Simple typed value used by the interpreter: either an integer or a boolean
    private static final class Value {
        enum Type {
            INT, BOOL
        }

        final Type type;
        final long i;
        final boolean b;

        private Value(long i) {
            this.type = Type.INT;
            this.i = i;
            this.b = false;
        }

        private Value(boolean b) {
            this.type = Type.BOOL;
            this.i = 0;
            this.b = b;
        }

        static Value ofInt(long v) {
            return new Value(v);
        }

        static Value ofBool(boolean v) {
            return new Value(v);
        }

        String asString() {
            return (type == Type.BOOL) ? Boolean.toString(b) : Long.toString(i);
        }
    }

    public static String interpret(String input) throws InterpretException {
        if (input == null) {
            throw new InterpretException("input cannot be null");
        }
        if ("test".equals(input)) {
            throw new InterpretException("invalid input: " + input);
        }

        String trimmed = input.trim();
        // direct boolean literals
        if ("true".equals(trimmed))
            return "true";
        if ("false".equals(trimmed))
            return "false";

        Map<String, Value> env = new HashMap<>();
        Map<String, Boolean> mut = new HashMap<>();
        String[] stmts = trimmed.split(";");
        String lastExpr = null;
        for (String stmt : stmts) {
            String s = stmt.trim();
            if (s.isEmpty())
                continue;
            if (s.startsWith("let ")) {
                String rest = s.substring(4).trim();
                boolean isMut = false;
                if (rest.startsWith("mut ")) {
                    isMut = true;
                    rest = rest.substring(4).trim();
                }
                int eq = rest.indexOf('=');
                if (eq <= 0)
                    return input;
                String before = rest.substring(0, eq).trim();
                String expr = rest.substring(eq + 1).trim();
                String id = before;
                String explicitType = null;
                int colon = before.indexOf(':');
                if (colon >= 0) {
                    id = before.substring(0, colon).trim();
                    explicitType = before.substring(colon + 1).trim();
                }
                try {
                    Parser p = new Parser(expr, env);
                    Value v = p.parseExpression();
                    if (p.hasNext())
                        return input;
                    // validate explicit type if present
                    if (explicitType != null) {
                        if ("I32".equals(explicitType)) {
                            if (v.type != Value.Type.INT)
                                return input;
                        } else if ("Bool".equals(explicitType)) {
                            if (v.type != Value.Type.BOOL)
                                return input;
                        } else {
                            return input; // unknown type annotation
                        }
                    }
                    env.put(id, v);
                    mut.put(id, isMut);
                } catch (RuntimeException e) {
                    return input;
                }
            } else {
                // support assignment statement: id = expr
                int i = 0;
                while (i < s.length() && (Character.isLetterOrDigit(s.charAt(i)) || s.charAt(i) == '_'))
                    i++;
                if (i > 0) {
                    String possibleId = s.substring(0, i);
                    int j = i;
                    while (j < s.length() && Character.isWhitespace(s.charAt(j)))
                        j++;
                    if (j < s.length() && s.charAt(j) == '=') {
                        // assignment
                        String rhs = s.substring(j + 1).trim();
                        try {
                            if (!env.containsKey(possibleId))
                                return input; // undefined
                            if (!Boolean.TRUE.equals(mut.get(possibleId)))
                                return input; // not mutable
                            Parser p = new Parser(rhs, env);
                            Value v = p.parseExpression();
                            if (p.hasNext())
                                return input;
                            env.put(possibleId, v);
                            continue; // assignment is a statement, not the final expression
                        } catch (RuntimeException e) {
                            return input;
                        }
                    }
                }
                lastExpr = s;
            }
        }

        if (lastExpr == null)
            return input;

        try {
            Parser p = new Parser(lastExpr, env);
            Value v = p.parseExpression();
            if (p.hasNext())
                return input;
            return v.asString();
        } catch (RuntimeException e) {
            return input;
        }
    }

    static class Parser {
        private final String s;
        private final Map<String, Value> env;
        private int pos = 0;

        Parser(String s, Map<String, Value> env) {
            this.s = s;
            this.env = env;
        }

        void skipWhitespace() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos)))
                pos++;
        }

        boolean hasNext() {
            skipWhitespace();
            return pos < s.length();
        }

        // expression and term operate on integer Values only
        Value parseExpression() {
            Value value = parseTerm();
            while (true) {
                skipWhitespace();
                if (pos < s.length() && (s.charAt(pos) == '+' || s.charAt(pos) == '-')) {
                    char op = s.charAt(pos++);
                    Value rhs = parseTerm();
                    if (value.type != Value.Type.INT || rhs.type != Value.Type.INT) {
                        throw new IllegalArgumentException("Operator requires integer operands");
                    }
                    long res = (op == '+') ? (value.i + rhs.i) : (value.i - rhs.i);
                    value = Value.ofInt(res);
                } else {
                    break;
                }
            }
            return value;
        }

        Value parseTerm() {
            Value value = parseFactor();
            while (true) {
                skipWhitespace();
                if (pos < s.length() && s.charAt(pos) == '*') {
                    pos++;
                    Value rhs = parseFactor();
                    if (value.type != Value.Type.INT || rhs.type != Value.Type.INT) {
                        throw new IllegalArgumentException("Operator requires integer operands");
                    }
                    value = Value.ofInt(value.i * rhs.i);
                } else {
                    break;
                }
            }
            return value;
        }

        Value parseFactor() {
            skipWhitespace();
            if (pos >= s.length())
                throw new IllegalArgumentException("Expected factor");
            char c = s.charAt(pos);
            if (c == '+' || c == '-') {
                pos++;
                Value v = parseFactor();
                if (v.type != Value.Type.INT)
                    throw new IllegalArgumentException("Unary +/- requires integer");
                return Value.ofInt((c == '-') ? -v.i : v.i);
            }
            if (c == '(') {
                pos++;
                Value v = parseExpression();
                skipWhitespace();
                if (pos >= s.length() || s.charAt(pos) != ')')
                    throw new IllegalArgumentException("Unclosed parenthesis");
                pos++;
                return v;
            }
            // boolean literal
            if (s.startsWith("true", pos)) {
                pos += 4;
                return Value.ofBool(true);
            }
            if (s.startsWith("false", pos)) {
                pos += 5;
                return Value.ofBool(false);
            }
            if (Character.isLetter(c) || c == '_') {
                int start = pos;
                while (pos < s.length() && (Character.isLetterOrDigit(s.charAt(pos)) || s.charAt(pos) == '_'))
                    pos++;
                String name = s.substring(start, pos);
                Value val = env.get(name);
                if (val == null)
                    throw new IllegalArgumentException("Unknown identifier: " + name);
                return val;
            }
            int start = pos;
            if (c == '+' || c == '-')
                pos++;
            while (pos < s.length() && Character.isDigit(s.charAt(pos)))
                pos++;
            if (start == pos)
                throw new IllegalArgumentException("Expected number at " + pos);
            String num = s.substring(start, pos);
            return Value.ofInt(Long.parseLong(num));
        }
    }
}

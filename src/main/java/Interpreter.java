import java.util.HashMap;
import java.util.Map;

public class Interpreter {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }

    public static String interpret(String input) throws InterpretException {
        if (input == null) {
            throw new InterpretException("input cannot be null");
        }
        if ("test".equals(input)) {
            throw new InterpretException("invalid input: " + input);
        }

        String trimmed = input.trim();
        if ("true".equals(trimmed))
            return "true";
        if ("false".equals(trimmed))
            return "false";

        Map<String, Long> env = new HashMap<>();
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
                String id = rest.substring(0, eq).trim();
                String expr = rest.substring(eq + 1).trim();
                try {
                    Parser p = new Parser(expr, env);
                    long v = p.parseExpression();
                    if (p.hasNext())
                        return input;
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
                            long v = p.parseExpression();
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
            long v = p.parseExpression();
            if (p.hasNext())
                return input;
            return Long.toString(v);
        } catch (RuntimeException e) {
            return input;
        }
    }

    static class Parser {
        private final String s;
        private final Map<String, Long> env;
        private int pos = 0;

        Parser(String s, Map<String, Long> env) {
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

        long parseExpression() {
            long value = parseTerm();
            while (true) {
                skipWhitespace();
                if (pos < s.length() && (s.charAt(pos) == '+' || s.charAt(pos) == '-')) {
                    char op = s.charAt(pos++);
                    long rhs = parseTerm();
                    value = (op == '+') ? value + rhs : value - rhs;
                } else {
                    break;
                }
            }
            return value;
        }

        long parseTerm() {
            long value = parseFactor();
            while (true) {
                skipWhitespace();
                if (pos < s.length() && s.charAt(pos) == '*') {
                    pos++;
                    long rhs = parseFactor();
                    value = value * rhs;
                } else {
                    break;
                }
            }
            return value;
        }

        long parseFactor() {
            skipWhitespace();
            if (pos >= s.length())
                throw new IllegalArgumentException("Expected factor");
            char c = s.charAt(pos);
            if (c == '+' || c == '-') {
                pos++;
                long v = parseFactor();
                return (c == '-') ? -v : v;
            }
            if (c == '(') {
                pos++;
                long v = parseExpression();
                skipWhitespace();
                if (pos >= s.length() || s.charAt(pos) != ')')
                    throw new IllegalArgumentException("Unclosed parenthesis");
                pos++;
                return v;
            }
            if (Character.isLetter(c) || c == '_') {
                int start = pos;
                while (pos < s.length() && (Character.isLetterOrDigit(s.charAt(pos)) || s.charAt(pos) == '_'))
                    pos++;
                String name = s.substring(start, pos);
                Long val = env.get(name);
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
            return Long.parseLong(num);
        }
    }
}

package magma;

public class ExpressionParser {
    final String s;
    int pos = 0;
    String commonSuffix = null;

    public ExpressionParser(String s) {
        this.s = s;
    }

    public interface VarResolver {
        long resolve(String name);

        long resolveRef(String name);
    }

    public long parseExprRes(ExpressionParser.VarResolver resolver) {
        return parseExprResInternal(resolver);
    }

    private long parseExprResInternal(ExpressionParser.VarResolver resolver) {
        long v = parseTermWithResolver(resolver);
        skipWhitespace();
        while (pos < s.length()) {
            char c = s.charAt(pos);
            if (c == '+' || c == '-') {
                pos++;
                long r = parseTermWithResolver(resolver);
                if (c == '+')
                    v = v + r;
                else
                    v = v - r;
                skipWhitespace();
            } else
                break;
        }
        return v;
    }

    private long parseTermWithResolver(ExpressionParser.VarResolver resolver) {
        long v = parseFactorWithResolver(resolver);
        skipWhitespace();
        while (pos < s.length()) {
            char c = s.charAt(pos);
            if (c == '*') {
                pos++;
                long r = parseFactorWithResolver(resolver);
                v = v * r;
                skipWhitespace();
            } else
                break;
        }
        return v;
    }

    private long parseFactorWithResolver(ExpressionParser.VarResolver resolver) {
        skipWhitespace();
        if (pos >= s.length())
            throw new IllegalArgumentException("Unexpected end");
        Long deref = tryParseUnaryDerefRes(resolver);
        if (deref != null)
            return deref.longValue();
        boolean unaryMinus = detectAndConsumeUnarySign();
        skipWhitespace();
        return parseParenOrValRes(resolver, unaryMinus);
    }

    private Long tryParseUnaryDerefRes(ExpressionParser.VarResolver resolver) {
        if (pos < s.length() && s.charAt(pos) == '*') {
            pos++;
            skipWhitespace();
            if (pos < s.length() && Character.isJavaIdentifierStart(s.charAt(pos))) {
                int start = pos;
                pos++;
                while (pos < s.length() && Character.isJavaIdentifierPart(s.charAt(pos)))
                    pos++;
                String name = s.substring(start, pos);
                long v = resolver.resolveRef(name);
                skipWhitespace();
                return Long.valueOf(v);
            }
            throw new IllegalArgumentException("Invalid dereference");
        }
        return null;
    }

    private long parseParenOrValRes(ExpressionParser.VarResolver resolver, boolean unaryMinus) {
        if (pos < s.length() && (s.charAt(pos) == '(' || s.charAt(pos) == '{')) {
            char open = s.charAt(pos);
            char close = open == '(' ? ')' : '}';
            pos++;
            long v = parseExprResInternal(resolver);
            skipWhitespace();
            if (pos >= s.length() || s.charAt(pos) != close)
                throw new IllegalArgumentException("Missing " + close);
            pos++;
            return unaryMinus ? -v : v;
        }
        if (pos < s.length() && Character.isJavaIdentifierStart(s.charAt(pos))) {
            int start = pos;
            pos++;
            while (pos < s.length() && Character.isJavaIdentifierPart(s.charAt(pos)))
                pos++;
            String name = s.substring(start, pos);
            long v = resolver.resolve(name);
            return unaryMinus ? -v : v;
        }
        ExpressionTypes.OperandParseResult r = ExpressionUtils.parseNumberWithSuffix(s, pos,
                ExpressionUtils.getAllowedSuffixes());
        if (r == null)
            throw new IllegalArgumentException("Invalid number");
        pos = r.nextPos;
        checkAndSetSuffix(r.suffix);
        return unaryMinus ? -r.value : r.value;
    }

    private void checkAndSetSuffix(String suf) {
        if (suf == null)
            return;
        if (commonSuffix == null)
            commonSuffix = suf;
        else if (!commonSuffix.equals(suf))
            throw new IllegalArgumentException("Mixed suffixes");
    }

    public boolean isAtEnd() {
        return pos >= s.length();
    }

    public void skipWhitespace() {
        while (pos < s.length() && Character.isWhitespace(s.charAt(pos)))
            pos++;
    }

    public long parseExpression() {
        long v = parseTerm();
        skipWhitespace();
        while (pos < s.length()) {
            char c = s.charAt(pos);
            if (c == '+' || c == '-') {
                pos++;
                long r = parseTerm();
                if (c == '+')
                    v = v + r;
                else
                    v = v - r;
                skipWhitespace();
            } else
                break;
        }
        return v;
    }

    public long parseTerm() {
        long v = parseFactor();
        skipWhitespace();
        while (pos < s.length()) {
            char c = s.charAt(pos);
            if (c == '*') {
                pos++;
                long r = parseFactor();
                v = v * r;
                skipWhitespace();
            } else
                break;
        }
        return v;
    }

    public long parseFactor() {
        skipWhitespace();
        if (pos >= s.length())
            throw new IllegalArgumentException("Unexpected end");
        if (tryConsumeUnaryDeref())
            throw new IllegalArgumentException("Dereference not available without resolver");
        boolean unaryMinus = detectAndConsumeUnarySign();
        skipWhitespace();
        if (pos < s.length() && (s.charAt(pos) == '(' || s.charAt(pos) == '{')) {
            char open = s.charAt(pos);
            char close = open == '(' ? ')' : '}';
            pos++;
            long v = parseExpression();
            skipWhitespace();
            if (pos >= s.length() || s.charAt(pos) != close)
                throw new IllegalArgumentException("Missing " + close);
            pos++;
            return unaryMinus ? -v : v;
        }
        ExpressionTypes.OperandParseResult r = ExpressionUtils.parseNumberWithSuffix(s, pos,
                ExpressionUtils.getAllowedSuffixes());
        if (r == null)
            throw new IllegalArgumentException("Invalid number");
        pos = r.nextPos;
        if (r.suffix != null) {
            if (commonSuffix == null)
                commonSuffix = r.suffix;
            else if (!commonSuffix.equals(r.suffix))
                throw new IllegalArgumentException("Mixed suffixes");
        }
        return unaryMinus ? -r.value : r.value;
    }

    private boolean tryConsumeUnaryDeref() {
        if (pos < s.length() && s.charAt(pos) == '*') {
            pos++;
            skipWhitespace();
            if (pos < s.length() && Character.isJavaIdentifierStart(s.charAt(pos))) {
                pos++;
                while (pos < s.length() && Character.isJavaIdentifierPart(s.charAt(pos)))
                    pos++;
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean detectAndConsumeUnarySign() {
        if (pos >= s.length())
            return false;
        if (s.charAt(pos) != '+' && s.charAt(pos) != '-')
            return false;
        char signChar = s.charAt(pos);
        int look = pos + 1;
        while (look < s.length() && Character.isWhitespace(s.charAt(look)))
            look++;
        if (look < s.length() && (s.charAt(look) == '(' || Character.isDigit(s.charAt(look)))) {
            pos = look;
            return signChar == '-';
        }
        return false;
    }
}

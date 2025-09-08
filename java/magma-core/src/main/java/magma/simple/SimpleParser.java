package magma.simple;

import magma.AstNode;
import magma.BinaryAst;
import magma.LiteralAst;
import magma.Parser;
import magma.Token;

/** Minimal parser that recognizes a single integer literal token. */
public class SimpleParser implements Parser {
    @Override
    public AstNode parse(Token[] tokens) {
        if (tokens == null || tokens.length == 0)
            return null;
        // Handle a single FNDEF token which may contain a tiny function like
        // "fn main() : I32 => { return 42; }". We only support extracting a
        // literal integer return from main for the stub pipeline tests.
        if (tokens.length == 1 && "FNDEF".equals(tokens[0].type)) {
            String t = tokens[0].text.trim();
            // Very small regex to capture: fn main() : I32 => { return 42; }
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                    "^fn\\s+main\\s*\\(\\s*\\)\\s*:\\s*(I32|i32)\\s*=>\\s*\\{\\s*return\\s+([0-9]+)(I32|i32)?\\s*;\\s*}\\s*$");
            java.util.regex.Matcher m = p.matcher(t);
            if (m.matches()) {
                String num = m.group(2);
                try {
                    long v = Long.parseLong(num);
                    return new LiteralAst((int) v, "i32");
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        }
        // Handle binary addition: INT PLUS INT
        if (tokens.length == 3 && "INT".equals(tokens[0].type) && "PLUS".equals(tokens[1].type)
                && "INT".equals(tokens[2].type)) {
            String a = tokens[0].text;
            String b = tokens[2].text;
            boolean as = a.endsWith("I32") || a.endsWith("i32");
            boolean bs = b.endsWith("I32") || b.endsWith("i32");
            String an = as ? a.substring(0, a.length() - 3) : a;
            String bn = bs ? b.substring(0, b.length() - 3) : b;
            try {
                long va = Long.parseLong(an);
                long vb = Long.parseLong(bn);
                LiteralAst la = new LiteralAst((int) va, "i32");
                LiteralAst lb = new LiteralAst((int) vb, "i32");
                return new BinaryAst(la, lb);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        Token t = tokens[0];
        if ("INT".equals(t.type)) {
            String txt = t.text;
            boolean hasSuffix = txt.endsWith("I32") || txt.endsWith("i32");
            String num = hasSuffix ? txt.substring(0, txt.length() - 3) : txt;
            try {
                long v = Long.parseLong(num);
                return new LiteralAst((int) v, "i32");
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}

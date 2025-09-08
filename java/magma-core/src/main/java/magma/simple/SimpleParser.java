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

            // Manual, small parser for pattern: fn main() : I32 => { return 42; }
            // We avoid java.util.regex usage intentionally.
            String cursor = t;
            // Expect prefix 'fn'
            if (!cursor.startsWith("fn"))
                return null;
            cursor = cursor.substring(2).trim();

            // Expect 'main'
            if (!cursor.startsWith("main"))
                return null;
            cursor = cursor.substring(4).trim();

            // Expect '()'
            if (!cursor.startsWith("()"))
                return null;
            cursor = cursor.substring(2).trim();

            // Expect ':' then type name (I32 or i32) then '=>'
            if (!cursor.startsWith(":"))
                return null;
            cursor = cursor.substring(1).trim();

            // Read type token
            String typeToken;
            if (cursor.toLowerCase().startsWith("i32")) {
                typeToken = "i32";
                cursor = cursor.substring(3).trim();
            } else {
                return null; // only support i32 for now
            }

            // Expect '=>'
            if (!cursor.startsWith("=>"))
                return null;
            cursor = cursor.substring(2).trim();

            // Expect '{' ... '}' block
            if (!cursor.startsWith("{"))
                return null;
            if (!cursor.endsWith("}"))
                return null;
            String body = cursor.substring(1, cursor.length() - 1).trim();

            // Expect 'return <number>;' inside body (allow optional I32 suffix on the
            // number)
            if (!body.startsWith("return ") || !body.endsWith(";"))
                return null;
            String retExpr = body.substring(7, body.length() - 1).trim();
            // Strip optional I32/i32 suffix
            if (retExpr.endsWith("I32") || retExpr.endsWith("i32")) {
                retExpr = retExpr.substring(0, retExpr.length() - 3).trim();
            }
            try {
                long v = Long.parseLong(retExpr);
                return new LiteralAst((int) v, typeToken);
            } catch (NumberFormatException e) {
                return null;
            }
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

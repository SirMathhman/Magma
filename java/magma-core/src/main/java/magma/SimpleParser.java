package magma;

/** Minimal parser that recognizes a single integer literal token. */
public class SimpleParser implements Parser {
    @Override
    public AstNode parse(Token[] tokens) {
        if (tokens == null || tokens.length == 0) return null;
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

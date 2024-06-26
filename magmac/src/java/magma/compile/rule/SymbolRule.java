package magma.compile.rule;

public final class SymbolRule extends FilterRule {
    public SymbolRule(Rule child) {
        super(child);
    }

    private static boolean isValidDigit(int i, char c) {
        return i != 0 && Character.isDigit(c);
    }

    private static boolean isUnderscore(char c) {
        return c == '_';
    }

    @Override
    protected String computeMessage() {
        return "Not a symbol.";
    }

    @Override
    protected boolean filter(String input) {
        if (input.isEmpty()) {
            return false;
        }

        int i = 0;
        while (i < input.length()) {
            var c = input.charAt(i);
            if (Character.isLetter(c) || isUnderscore(c) || isDollar(c) || isValidDigit(i, c)) {
                i++;
                continue;
            }

            return false;
        }

        return true;
    }

    private static boolean isDollar(char c) {
        return c == '$';
    }
}

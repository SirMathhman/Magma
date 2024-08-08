package magma.app.compile.lang;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.RuleResult;

public final class SymbolRule implements Rule {
    private final Rule value;

    public SymbolRule(Rule value) {
        this.value = value;
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        if (isSymbol(input)) {
            return value.parse(input);
        } else {
            return RuleResult.RuleResult(Err.Err(new ParseError("Not a symbol", input)));
        }
    }

    private boolean isSymbol(String input) {
        int i = 0;
        while (i < input.length()) {
            var c = input.charAt(i);
            if (Character.isLetter(c) || c == '_') {
                i++;
                continue;
            }
            if (Character.isDigit(c) && i != 0) {
                i++;
                continue;
            }

            return false;
        }

        return true;
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return value.generate(node);
    }
}

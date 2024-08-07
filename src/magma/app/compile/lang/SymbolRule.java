package magma.app.compile.lang;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.RuleResult;

public record SymbolRule(Rule value) implements Rule {
    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        if(isSymbol(input)) {
            return value.parse(input);
        } else {
            return RuleResult.RuleResult(Err.Err(new ParseError("Not a symbol", input)));
        }
    }

    private boolean isSymbol(String input) {
        for (int i = 0; i < input.length(); i++) {
            var c = input.charAt(i);
            if(!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return value.generate(node);
    }
}

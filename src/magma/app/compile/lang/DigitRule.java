package magma.app.compile.lang;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.RuleResult;

import java.util.Objects;

public final class DigitRule implements Rule {
    private final Rule value;

    public DigitRule(Rule value) {
        this.value = value;
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        if (isSymbol(input)) {
            return value.parse(input);
        } else {
            return RuleResult.RuleResult(Err.Err(new ParseError("Not digits", input)));
        }
    }

    private boolean isSymbol(String input) {
        int i = 0;
        while (i < input.length()) {
            var c = input.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
            i++;
        }

        return true;
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return value.generate(node);
    }

    public Rule value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DigitRule) obj;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "DigitRule[" +
               "value=" + value + ']';
    }

}

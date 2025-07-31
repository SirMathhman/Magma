package magma.rule;

import magma.node.Node;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

public final class PrefixRule implements Rule {
    private final Rule rule;
    private final String prefix;

    public PrefixRule(final String prefix, final Rule rule) {
        this.rule = rule;
        this.prefix = prefix;
    }

    @Override
    public Result<String, String> generate(final Node node) {
        Result<String, String> result = this.rule.generate(node); if (result.isErr()) {
            return result;
        } return new Ok<>(this.prefix + result.unwrap());
    }
    
    @Override
    public Result<Node, String> lex(final String input) {
        if (!input.startsWith(this.prefix)) {
            return new Err<>("Input does not start with prefix: " + this.prefix);
        }
        
        String content = input.substring(this.prefix.length());
        return this.rule.lex(content);
    }
}
package magma.rule;

import magma.node.Node;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

public final class SuffixRule implements Rule {
    private final Rule rule;
    private final String suffix;

    public SuffixRule(final Rule rule, final String suffix) {
        this.rule = rule;
        this.suffix = suffix;
    }

    @Override
    public Result<String, String> generate(final Node node) {
        Result<String, String> result = this.rule.generate(node); if (result.isErr()) {
            return result;
        } return new Ok<>(result.unwrap() + this.suffix);
    }
    
    @Override
    public Result<Node, String> lex(final String input) {
        if (!input.endsWith(this.suffix)) {
            return new Err<>("Input does not end with suffix: " + this.suffix);
        }
        
        String content = input.substring(0, input.length() - this.suffix.length());
        return this.rule.lex(content);
    }
}
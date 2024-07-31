package magma;

import java.util.List;
import java.util.Optional;

public record DisjunctionRule(List<Rule> rules) implements Rule {
    private Optional<Node> parse0(String input) {
        for (Rule rule : rules) {
            var result = rule.parse(input).findValue();
            if (result.isPresent()) return result;
        }

        return Optional.empty();
    }

    private Optional<String> generate0(Node node) {
        for (Rule rule : rules) {
            var generated = rule.generate(node).findValue();
            if(generated.isPresent()) return generated;
        }

        return Optional.empty();
    }

    @Override
    public Result<Node, ParseException> parse(String input) {
        return parse0(input)
                .<Result<Node, ParseException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new ParseException("Invalid input", input)));
    }

    @Override
    public Result<String, GeneratingException> generate(Node node) {
        return generate0(node)
                .<Result<String, GeneratingException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new GeneratingException("Invalid node", node)));
    }
}

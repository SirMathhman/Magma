package magma.rule;

import magma.node.Node;
import magma.result.Err;
import magma.result.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A Rule that contains a list of rules and tries each one in sequence until one succeeds.
 * For both lex and generate operations, it returns the first successful result.
 */
public final class OrRule implements Rule {
    private final List<Rule> rules;

    /**
     * Creates a new FirstMatchRule with the specified list of rules.
     *
     * @param rules the rules to try in sequence
     */
    public OrRule(final List<Rule> rules) {
        this.rules = List.copyOf(rules); // Create an immutable copy of the list
    }

    /**
     * Creates a new FirstMatchRule with the specified rules.
     *
     * @param rules the rules to try in sequence
     */
    public OrRule(final Rule... rules) {
        this.rules = List.of(rules); // Create an immutable list from the array
    }

    @Override
    public Result<String, String> generate(final Node node) {
        // Try each rule in sequence until one succeeds
        List<String> errors = new ArrayList<>();
        
        for (final Rule rule : this.rules) {
            final Result<String, String> result = rule.generate(node); if (result.isOk()) {
                return result;
            } errors.add(result.unwrapErr());
        }

        // If no rule succeeds, return an error with all the error messages
        return new Err<>("All rules failed: " + errors.stream().collect(Collectors.joining(", ")));
    }

    @Override
    public Result<Node, String> lex(final String input) {
        // Try each rule in sequence until one succeeds
        List<String> errors = new ArrayList<>();
        
        for (final Rule rule : this.rules) {
            final Result<Node, String> result = rule.lex(input); if (result.isOk()) {
                return result;
            } errors.add(result.unwrapErr());
        }

        // If no rule succeeds, return an error with all the error messages
        return new Err<>("All rules failed: " + errors.stream().collect(Collectors.joining(", ")));
    }
}
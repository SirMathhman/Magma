package magma.rule;

import magma.node.Node;

import java.util.List;
import java.util.Optional;

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
    public Optional<String> generate(final Node node) {
        // Try each rule in sequence until one succeeds
        for (final Rule rule : this.rules) {
            final Optional<String> result = rule.generate(node);
            if (result.isPresent()) {
                return result;
            }
        }
        
        // If no rule succeeds, return empty
        return Optional.empty();
    }

    @Override
    public Optional<Node> lex(final String input) {
        // Try each rule in sequence until one succeeds
        for (final Rule rule : this.rules) {
            final Optional<Node> result = rule.lex(input);
            if (result.isPresent()) {
                return result;
            }
        }
        
        // If no rule succeeds, return empty
        return Optional.empty();
    }
}
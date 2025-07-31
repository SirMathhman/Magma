package magma.rule;

import magma.node.Node;

import java.util.Optional;

/**
 * A Rule that attaches a type tag to nodes when lexing and checks for the type before generating.
 * This rule wraps another rule and adds/checks for a specific type tag.
 */
public final class TypeRule implements Rule {
    private final String type;
    private final Rule childRule;

    /**
     * Creates a new TypeRule with the specified type and child rule.
     *
     * @param type the type tag to attach/check for
     * @param childRule the rule to delegate to
     */
    public TypeRule(final String type, final Rule childRule) {
        this.type = type;
        this.childRule = childRule;
    }

    @Override
    public Optional<String> generate(final Node node) {
        // Only generate if the node has the expected type
        if (!node.is(this.type)) {
            return Optional.empty();
        }
        
        // Delegate to the child rule
        return this.childRule.generate(node);
    }

    @Override
    public Optional<Node> lex(final String input) {
        // Delegate to the child rule
        final Optional<Node> result = this.childRule.lex(input);
        
        // If the child rule successfully lexed the input, attach the type tag
        return result.map(node -> node.retype(this.type));
    }
}
package magma.rule;

import magma.error.CompileError;
import magma.error.StringContext;
import magma.node.Node;
import magma.result.Err;
import magma.result.Result;

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
    public Result<String, CompileError> generate(final Node node) {
        // Only generate if the node has the expected type
        if (!node.is(this.type)) {
            return new Err<>(
                new CompileError("Node does not have the expected type: " + this.type, new StringContext(this.type)));
        }
        
        // Delegate to the child rule
        return this.childRule.generate(node);
    }

    @Override
    public Result<Node, CompileError> lex(final String input) {
        // Delegate to the child rule
        final Result<Node, CompileError> result = this.childRule.lex(input);
        
        // If the child rule successfully lexed the input, attach the type tag
        if (result.isErr()) return result;

        return result.mapValue(node -> node.retype(this.type));
    }
}
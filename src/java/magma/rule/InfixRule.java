package magma.rule;

import magma.node.MapNode;
import magma.node.Node;

import java.util.Optional;

public final class InfixRule implements Rule {
    private final Rule leftRule;
    private final Rule rightRule;
    private final String infix;

    public InfixRule(final Rule leftRule, final Rule rightRule, final String infix) {
        this.leftRule = leftRule;
        this.rightRule = rightRule;
        this.infix = infix;
    }

    @Override
    public Optional<String> generate(final Node node) {
        final Optional<String> leftResult = this.leftRule.generate(node);
        final Optional<String> rightResult = this.rightRule.generate(node);
        
        if (leftResult.isPresent() && rightResult.isPresent()) {
            return Optional.of(leftResult.get() + this.infix + rightResult.get());
        } else if (leftResult.isPresent()) {
            return leftResult;
        } else if (rightResult.isPresent()) {
            return rightResult;
        } else {
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Node> lex(final String input) {
        if (this.infix.isEmpty()) {
            // Try to lex with left rule first, then right rule if left fails
            Optional<Node> leftResult = this.leftRule.lex(input);
            if (leftResult.isPresent()) {
                return leftResult;
            }
            return this.rightRule.lex(input);
        }
        
        int infixIndex = input.indexOf(this.infix);
        if (infixIndex == -1) {
            // If infix not found, try each rule separately
            Optional<Node> leftResult = this.leftRule.lex(input);
            if (leftResult.isPresent()) {
                return leftResult;
            }
            return this.rightRule.lex(input);
        }
        
        String leftPart = input.substring(0, infixIndex);
        String rightPart = input.substring(infixIndex + this.infix.length());
        
        Optional<Node> leftNode = this.leftRule.lex(leftPart);
        Optional<Node> rightNode = this.rightRule.lex(rightPart);
        
        if (leftNode.isPresent() && rightNode.isPresent()) {
            // Merge the two nodes by copying all properties from both nodes
            MapNode result = new MapNode();
            
            // Copy all properties from left node
            copyProperties(leftNode.get(), result);
            
            // Copy all properties from right node
            copyProperties(rightNode.get(), result);
            
            return Optional.of(result);
        } else if (leftNode.isPresent()) {
            return leftNode;
        } else if (rightNode.isPresent()) {
            return rightNode;
        }
        
        return Optional.empty();
    }
    
    // Helper method to copy all properties from one node to another
    private void copyProperties(Node source, MapNode target) {
        // We don't have a way to iterate over all properties in a Node,
        // so we'll try common property names used in our application
        copyProperty(source, target, "name");
        copyProperty(source, target, "body");
        copyProperty(source, target, "left");
        copyProperty(source, target, "right");
        copyProperty(source, target, "test");
    }
    
    private void copyProperty(Node source, MapNode target, String key) {
        source.findString(key).ifPresent(value -> target.withString(key, value));
    }
}
package magma.app.rule;

import magma.app.Node;
import magma.app.Rule;
import magma.app.maybe.Attachable;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.MaybeString;
import magma.app.maybe.node.EmptyNode;
import magma.app.maybe.node.PresentNode;
import magma.app.maybe.string.EmptyString;
import magma.app.maybe.string.PresentString;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public record OrRule(List<Rule<Node, MaybeNode, MaybeString>> rules) implements Rule<Node, MaybeNode, MaybeString> {
    @Override
    public MaybeString generate(Node node) {
        return this.or(rule1 -> rule1.generate(node), PresentString::new, EmptyString::new);
    }

    private <MaybeValue extends Attachable<Value>, Value, Return> Return or(Function<Rule<Node, MaybeNode, MaybeString>, MaybeValue> mapper, Function<Value, Return> whenPresent, Supplier<Return> whenMissing) {
        return this.rules.stream().map(mapper).reduce(new OrState<Value>(), (orState, maybeString) -> maybeString.attachTo(orState), (_, next) -> next).unwrap().map(whenPresent).orElseGet(whenMissing);
    }

    @Override
    public MaybeNode lex(String input) {
        return this.<Attachable<Node>, Node, MaybeNode>or(rule1 -> rule1.lex(input), PresentNode::new, EmptyNode::new);
    }
}

package magma.app.compile;

import magma.api.collect.iter.Iterable;
import magma.api.collect.list.Lists;

public final class DivideRule<Node extends NodeWithNodeLists<Node>, Error, NodeResult, StringResult extends AppendableStringResult<StringResult>> implements Rule<Node, NodeResult, StringResult> {
    private final String key;
    private final Rule<Node, NodeResult, StringResult> rule;
    private final NodeFactory<Node> nodeFactory;
    private final ResultFactory<Node, Error, NodeResult, StringResult> resultFactory;

    public DivideRule(String key, Rule<Node, NodeResult, StringResult> rule, NodeFactory<Node> nodeFactory, ResultFactory<Node, Error, NodeResult, StringResult> resultFactory) {
        this.key = key;
        this.rule = rule;
        this.nodeFactory = nodeFactory;
        this.resultFactory = resultFactory;
    }

    private static Iterable<String> divide(CharSequence input) {
        DivideState current = new MutableDivideState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance()
                .segments();
    }

    private static DivideState fold(DivideState divideState, char c) {
        final var appended = divideState.append(c);
        if (c == ';' && appended.isLevel())
            return appended.advance();

        if (c == '{')
            return appended.enter();

        if (c == '}')
            return appended.exit();

        return appended;
    }

    @Override
    public NodeResult lex(String input) {
        return divide(input).iter()
                .fold(this.resultFactory.fromEmptyNodeList(),
                        (maybeCurrent, element) -> maybeCurrent.add(() -> this.rule.lex(element)))
                .toNode(this.nodeFactory, this.key);
    }

    @Override
    public StringResult generate(Node node) {
        return node.findNodeList(this.key)
                .orElse(Lists.empty())
                .iter()
                .fold(this.resultFactory.fromEmptyString(), this::foldString);
    }

    private StringResult foldString(StringResult maybeCurrent, Node element) {
        return maybeCurrent.appendResult(() -> this.rule.generate(element));
    }
}
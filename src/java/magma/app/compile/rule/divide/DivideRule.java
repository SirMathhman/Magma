package magma.app.compile.rule.divide;

import magma.app.compile.AppendableStringResult;
import magma.app.compile.ResultFactory;
import magma.app.compile.node.NodeWithNodeLists;
import magma.app.compile.rule.NodeFactory;
import magma.app.compile.rule.Rule;

import java.util.ArrayList;
import java.util.List;

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

    private static List<String> divide(CharSequence input) {
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
        return divide(input).stream()
                .reduce(this.resultFactory.fromEmptyNodeList(),
                        (maybeCurrent, element) -> maybeCurrent.add(() -> this.rule.lex(element)),
                        (_, next) -> next)
                .toNode(this.nodeFactory, this.key);
    }

    @Override
    public StringResult generate(Node node) {
        return node.findNodeList(this.key)
                .orElse(new ArrayList<>())
                .stream()
                .reduce(this.resultFactory.fromEmptyString(), this::foldString, (_, next) -> next);
    }

    private StringResult foldString(StringResult maybeCurrent, Node element) {
        return maybeCurrent.appendResult(() -> this.rule.generate(element));
    }
}
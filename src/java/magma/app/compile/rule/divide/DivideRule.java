package magma.app.compile.rule.divide;

import magma.app.compile.error.FormattedError;
import magma.app.compile.error.list.NodeListErr;
import magma.app.compile.error.list.NodeListOk;
import magma.app.compile.error.list.NodeListResult;
import magma.app.compile.error.node.NodeErr;
import magma.app.compile.error.node.NodeOk;
import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringOk;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.node.Node;
import magma.app.compile.rule.NodeFactory;
import magma.app.compile.rule.Rule;

import java.util.ArrayList;
import java.util.List;

public final class DivideRule implements Rule<Node, NodeResult<Node>, StringResult> {
    private final String key;
    private final Rule<Node, NodeResult<Node>, StringResult> rule;
    private final NodeFactory<Node> factory;

    public DivideRule(String key, Rule<Node, NodeResult<Node>, StringResult> rule, NodeFactory<Node> factory) {
        this.key = key;
        this.rule = rule;
        this.factory = factory;
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
    public NodeResult<Node> lex(String input) {
        NodeListResult listFormattedErrorResult = divide(input).stream()
                .<NodeListResult>reduce(new NodeListOk(),
                        (maybeCurrent, element) -> maybeCurrent.add(() -> this.rule.lex(element)),
                        (_, next) -> next);
        return switch (listFormattedErrorResult) {
            case NodeListErr(FormattedError error) -> new NodeErr(error);
            case NodeListOk(
                    List<Node> value
            ) -> new NodeOk(this.factory.create()
                    .withNodeList(this.key, value));
        };
    }

    @Override
    public StringResult generate(Node node) {
        return node.findNodeList(this.key)
                .orElse(new ArrayList<>())
                .stream()
                .reduce(new StringOk(), this::foldString, (_, next) -> next);
    }

    private StringResult foldString(StringResult maybeCurrent, Node element) {
        return maybeCurrent.appendResult(() -> this.rule.generate(element));
    }
}
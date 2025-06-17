package magma.app.compile.rule.divide;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.NodeErr;
import magma.app.compile.error.NodeOk;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.StringOk;
import magma.app.compile.error.StringResult;
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
        Result<List<Node>, FormattedError> listFormattedErrorResult = divide(input).stream()
                .reduce(new Ok<>(new ArrayList<>()), this::foldElement, (_, next) -> next);
        return switch (listFormattedErrorResult) {
            case Err<List<Node>, FormattedError>(FormattedError error) -> new NodeErr(error);
            case Ok<List<Node>, FormattedError>(
                    List<Node> value
            ) -> new NodeOk(this.factory.create()
                    .withNodeList(this.key, value));
        };
    }

    private Result<List<Node>, FormattedError> foldElement(Result<List<Node>, FormattedError> maybeCurrent, String element) {
        return switch (maybeCurrent) {
            case Err<List<Node>, FormattedError>(FormattedError error1) -> new Err<>(error1);
            case Ok<List<Node>, FormattedError>(List<Node> value1) -> this.rule.lex(element)
                    .appendTo(value1);
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
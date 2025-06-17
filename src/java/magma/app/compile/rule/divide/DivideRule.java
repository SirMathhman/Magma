package magma.app.compile.rule.divide;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;
import magma.app.compile.rule.Rule;

import java.util.ArrayList;
import java.util.List;

public record DivideRule(String key,
                         Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> rule) implements Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> {
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
    public Result<Node, FormattedError> lex(String input) {
        Result<List<Node>, FormattedError> listFormattedErrorResult = divide(input).stream()
                .reduce(new Ok<>(new ArrayList<>()), this::foldElement, (_, next) -> next);
        return switch (listFormattedErrorResult) {
            case Err<List<Node>, FormattedError>(FormattedError error) -> new Err<>(error);
            case Ok<List<Node>, FormattedError>(
                    List<Node> value
            ) -> new Ok<>(new MapNode().withNodeList(this.key(), value));
        };
    }

    private Result<List<Node>, FormattedError> foldElement(Result<List<Node>, FormattedError> maybeCurrent, String element) {
        return switch (maybeCurrent) {
            case Err<List<Node>, FormattedError>(FormattedError error1) -> new Err<>(error1);
            case Ok<List<Node>, FormattedError>(
                    List<Node> value1
            ) -> {
                Result<Node, FormattedError> nodeFormattedErrorResult = this.rule.lex(element);
                yield this.getListFormattedErrorResult(value1, nodeFormattedErrorResult);
            }
        };
    }

    private Result<List<Node>, FormattedError> getListFormattedErrorResult(List<Node> value1, Result<Node, FormattedError> nodeFormattedErrorResult) {
        return switch (nodeFormattedErrorResult) {
            case Err<Node, FormattedError>(FormattedError error) -> new Err<>(error);
            case Ok<Node, FormattedError>(Node value) -> {
                value1.add(value);
                yield new Ok<>(value1);
            }
        };
    }

    @Override
    public Result<String, FormattedError> generate(Node node) {
        Result<StringBuilder, FormattedError> stringBuilderFormattedErrorResult = node.findNodeList(this.key)
                .orElse(new ArrayList<>())
                .stream()
                .reduce(new Ok<>(new StringBuilder()), this::foldString, (_, next) -> next);
        return switch (stringBuilderFormattedErrorResult) {
            case Err<StringBuilder, FormattedError>(FormattedError error) -> new Err<>(error);
            case Ok<StringBuilder, FormattedError>(
                    StringBuilder value
            ) -> new Ok<>(value.toString());
        };
    }

    private Result<StringBuilder, FormattedError> foldString(Result<StringBuilder, FormattedError> maybeCurrent, Node element) {
        return switch (maybeCurrent) {
            case Err<StringBuilder, FormattedError>(FormattedError error1) -> new Err<>(error1);
            case Ok<StringBuilder, FormattedError>(
                    StringBuilder value1
            ) -> {
                Result<String, FormattedError> stringFormattedErrorResult = this.rule.generate(element);
                yield this.getStringBuilderFormattedErrorResult(value1, stringFormattedErrorResult);
            }
        };
    }

    private Result<StringBuilder, FormattedError> getStringBuilderFormattedErrorResult(StringBuilder value1, Result<String, FormattedError> stringFormattedErrorResult) {
        return switch (stringFormattedErrorResult) {
            case Err<String, FormattedError>(FormattedError error) -> new Err<>(error);
            case Ok<String, FormattedError>(
                    String value
            ) -> new Ok<>(value1.append(value));
        };
    }
}
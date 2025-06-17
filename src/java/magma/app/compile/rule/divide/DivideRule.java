package magma.app.compile.rule.divide;

import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.CompileError;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;
import magma.app.compile.rule.Rule;

import java.util.ArrayList;
import java.util.List;

public record DivideRule(String key, Rule rule) implements Rule {
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
    public Result<Node, CompileError> lex(String input) {
        return divide(input).stream()
                .reduce(new Ok<>(new ArrayList<>()), this::foldElement, (_, next) -> next)
                .mapValue(children -> new MapNode().withNodeList(this.key(), children));
    }

    private Result<List<Node>, CompileError> foldElement(Result<List<Node>, CompileError> maybeCurrent, String element) {
        return maybeCurrent.flatMapValue(current -> DivideRule.this.rule.lex(element)
                .mapValue(result -> {
                    current.add(result);
                    return current;
                }));
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return node.findNodeList(this.key)
                .orElse(new ArrayList<>())
                .stream()
                .reduce(new Ok<>(new StringBuilder()), this::foldString, (_, next) -> next)
                .mapValue(StringBuilder::toString);
    }

    private Result<StringBuilder, CompileError> foldString(Result<StringBuilder, CompileError> maybeCurrent, Node element) {
        return maybeCurrent.flatMapValue(current -> this.rule.generate(element)
                .mapValue(current::append));
    }
}
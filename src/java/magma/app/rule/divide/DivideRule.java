package magma.app.rule.divide;

import magma.CompileError;
import magma.api.Ok;
import magma.api.Result;
import magma.app.node.MapNode;
import magma.app.node.Node;
import magma.app.rule.Rule;

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
        if (c == ';')
            return appended.advance();

        return appended;
    }

    @Override
    public Result<Node, CompileError> lex(String input) {
        return divide(input).stream()
                .reduce(new Ok<>(new ArrayList<>()), this::foldElement, (_, next) -> next)
                .mapValue(children -> new MapNode().withNodeList(this.key(), children));
    }

    private Result<List<Node>, CompileError> foldElement(Result<List<Node>, CompileError> maybeCurrent, String element) {
        return maybeCurrent.flatMap(current -> DivideRule.this.rule.lex(element)
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
        return maybeCurrent.flatMap(current -> this.rule.generate(element)
                .mapValue(current::append));
    }
}
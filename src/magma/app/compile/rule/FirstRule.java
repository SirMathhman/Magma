package magma.app.compile.rule;

import magma.api.Tuple;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.CompileException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record FirstRule(Rule left, String slice, Rule right) implements Rule {
    static Optional<Tuple<String, String>> splitFirst(String slice, String input) {
        var index = input.indexOf(slice);
        if (index == -1) return Optional.empty();
        var left = input.substring(0, index);
        var right = input.substring(index + slice.length());
        return Optional.of(new Tuple<>(left, right));
    }

    private static HashMap<String, String> merge(Map<String, String> withModifiers, Map<String, String> wthName) {
        var merged = new HashMap<>(withModifiers);
        merged.putAll(wthName);
        return merged;
    }

    private Optional<Map<String, String>> parse0(String input) {
        return splitFirst(slice(), input).flatMap(tuple -> this.left().parse(tuple.left()).findValue().map(Node::strings).flatMap(withModifiers -> {
            return this.right().parse(tuple.right()).findValue().map(Node::strings)
                    .map(wthName -> merge(withModifiers, wthName));
        }));
    }

    private Optional<String> generate0(Map<String, String> node) {
        return left.generate(new Node(Optional.empty(), node)).findValue().flatMap(leftValue -> this.right().generate(new Node(Optional.empty(), node)).findValue().map(rightValue -> leftValue + slice + rightValue));
    }

    private Optional<Node> parse1(String input) {
        return parse0(input).map(strings -> new Node(Optional.empty(), strings));
    }

    private Optional<String> generate1(Node node) {
        return generate0(node.strings());
    }

    @Override
    public Result<Node, CompileException> parse(String input) {
        return parse1(input)
                .<Result<Node, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileException("Invalid input", input)));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return generate1(node)
                .<Result<String, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileException("Cannot generate", node.toString())));
    }
}
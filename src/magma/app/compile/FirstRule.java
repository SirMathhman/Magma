package magma.app.compile;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Optionals;
import magma.api.Result;

import java.util.Optional;

public record FirstRule(Rule left, String slice, Rule right) implements Rule {
    private Optional<Node> parse0(String input) {
        var index = input.indexOf(slice);
        if (index == -1) return Optional.empty();

        var leftSlice = input.substring(0, index);
        var rightSlice = input.substring(index + slice.length());

        return Optionals.and(left.parse(leftSlice).findValue(), () -> right.parse(rightSlice).findValue()).map(tuple -> tuple.left().merge(tuple.right()));
    }

    private Optional<String> generate0(Node node) {
        return Optionals.and(left.generate(node).findValue(), () -> right.generate(node).findValue()).map(tuple -> tuple.left() + slice + tuple.right());
    }

    @Override
    public Result<Node, CompileException> parse(String input) {
        return parse0(input)
                .<Result<Node, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileException("Invalid input", input)));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return generate0(node)
                .<Result<String, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new NodeException("Invalid node.", node)));
    }
}
package magma.app.compile;

import magma.api.Err;
import magma.api.Result;

public record FirstRule(Rule left, String slice, Rule right) implements Rule {
    private Result<Node, CompileException> parse1(String input) {
        var index = input.indexOf(slice);
        if (index == -1)
            return new Err<>(new CompileException("Slice '" + slice + "' was not present in input", input));

        var leftSlice = input.substring(0, index);
        var rightSlice = input.substring(index + slice.length());

        return left.parse(leftSlice).result()
                .mapErr(err -> new CompileException("Failed to parse left side", leftSlice, err))
                .and(() -> right.parse(rightSlice).result().mapErr(err -> new CompileException("Failed to parse right side", rightSlice, err)))
                .mapValue(tuple -> tuple.left().merge(tuple.right()));
    }

    private Result<String, CompileException> generate1(Node node) {
        return left.generate(node).result()
                .and(() -> right.generate(node).result())
                .mapValue(tuple -> tuple.left() + slice + tuple.right())
                .mapErr(err -> new NodeException("Failed to attach slice '" + slice + "'", node, err));
    }

    @Override
    public CompileResult<Node> parse(String input) {
        return new CompileResult<>(parse1(input));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return new CompileResult<>(generate1(node));
    }
}
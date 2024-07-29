package magma.app.compile;

import magma.api.Err;
import magma.api.Optionals;
import magma.api.Result;

import java.util.Optional;

public record FirstRule(Rule left, String slice, Rule right) implements Rule {
    @Override
    public Result<Node, CompileException> parse(String input) {
        var index = input.indexOf(slice);
        if (index == -1) return new Err<>(new CompileException("Slice '" + slice + "' was not present in input", input));

        var leftSlice = input.substring(0, index);
        var rightSlice = input.substring(index + slice.length());

        return left.parse(leftSlice)
                .mapErr(err -> new CompileException("Failed to parse left side", leftSlice, err))
                .and(() -> right.parse(rightSlice).mapErr(err -> new CompileException("Failed to parse right side", rightSlice, err)))
                .mapValue(tuple -> tuple.left().merge(tuple.right()));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return left.generate(node)
                .and(() -> right.generate(node))
                .mapValue(tuple -> tuple.left() + slice + tuple.right())
                .mapErr(err -> new NodeException("Failed to attach slice '" + slice + "'", node, err));
    }
}
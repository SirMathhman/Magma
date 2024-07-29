package magma.app.compile;

import magma.api.Err;

public record FirstRule(Rule left, String slice, Rule right) implements Rule {
    @Override
    public CompileResult<Node> parse(String input) {
        var index = input.indexOf(slice);
        if (index == -1)
            return new CompileResult<>(new Err<>(new CompileException("Slice '" + slice + "' was not present in input", input)));

        var leftSlice = input.substring(0, index);
        var rightSlice = input.substring(index + slice.length());

        var leftResult = left.parse(leftSlice).wrapErr(() -> new CompileException("Failed to parse left side", leftSlice));
        var rightResult = right.parse(rightSlice).wrapErr(() -> new CompileException("Failed to parse right side", rightSlice));
        return leftResult.and(rightResult, Node::merge);
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return left.generate(node)
                .and(right.generate(node), (left, right) -> left + slice + right)
                .wrapErr(() -> new NodeException("Failed to attach slice '" + slice + "'", node));
    }
}
package magma.app.compile.rule;

import magma.api.Tuple;
import magma.api.result.Err;
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

    @Override
    public Result<Node, CompileException> parse(String input) {
        return splitFirst(slice, input).map(this::process).orElseGet(() -> new Err<>(new CompileException("Slice '" + slice + "' not present", input)));
    }

    private Result<Node, CompileException> process(Tuple<String, String> tuple) {
        return left
                .parse(tuple.left()).and(() -> right.parse(tuple.right()))
                .mapValue(tuple0 -> tuple0.left().merge(tuple0.right()));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return left.generate(node)
                .and(() -> right.generate(node))
                .mapValue(tuple -> tuple.left() + slice + tuple.right());
    }
}
package magma.app.compile.rule;

import magma.app.compile.GenerateException;
import magma.app.compile.Node;
import magma.app.compile.ParseException;
import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public record FirstRule(Rule leftRule, String slice, Rule rightRule) implements Rule {
    private Optional<Node> parse0(String input) {
        var startIndex = input.indexOf(slice());
        if (startIndex == -1) return Optional.empty();
        var leftSlice = input.substring(0, startIndex);

        var leftResult = this.leftRule().parse(leftSlice).findValue();
        if (leftResult.isEmpty()) return Optional.empty();

        var rightSlice = input.substring(startIndex + slice.length());
        var withContentOptional = this.rightRule().parse(rightSlice).findValue();

        return withContentOptional.map(node -> leftResult.get().merge(node));
    }


    @Override
    public Result<Node, ParseException> parse(String input) {
        return parse0(input)
                .<Result<Node, ParseException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new ParseException("Invalid input", input)));
    }

    @Override
    public Result<String, GenerateException> generate(Node node) {
        return leftRule.generate(node)
                .and(() -> rightRule.generate(node))
                .mapValue(tuple -> tuple.left() + slice + tuple.right());
    }
}
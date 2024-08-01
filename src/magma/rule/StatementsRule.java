package magma.rule;

import magma.GenerateException;
import magma.Node;
import magma.ParseException;
import magma.Splitter;
import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.api.Tuple;

import java.util.ArrayList;
import java.util.List;

public record StatementsRule(Rule childRule, String propertyName) implements Rule {
    private static ArrayList<Node> add(Tuple<List<Node>, Node> tuple) {
        var copy = new ArrayList<>(tuple.left());
        copy.add(tuple.right());
        return copy;
    }

    @Override
    public Result<Node, ParseException> parse(String input) {
        var rootMembers = Splitter.splitRootMembers(input);
        Result<List<Node>, ParseException> childrenResult = new Ok<>(new ArrayList<>());
        for (var rootMember : rootMembers) {
            var stripped = rootMember.strip();
            if (stripped.isEmpty()) continue;

            childrenResult = childrenResult
                    .and(() -> childRule.parse(stripped).mapErr(err -> err))
                    .mapValue(StatementsRule::add);
        }

        return childrenResult.mapValue(children -> new Node().withNodeList(propertyName, children));
    }

    @Override
    public Result<String, GenerateException> generate(Node root) {
        var childrenOptional = root.findNodeList(propertyName());
        if (childrenOptional.isEmpty()) {
            return new Err<>(new GenerateException("Node list property '" + propertyName() + "' was not present", root));
        }

        Result<StringBuilder, GenerateException> builder = new Ok<>(new StringBuilder());
        for (Node child : childrenOptional.get()) {
            builder = builder
                    .and(() -> childRule().generate(child).mapErr(err -> err))
                    .mapValue(tuple -> tuple.left().append(tuple.right()));
        }

        return builder.mapValue(StringBuilder::toString);
    }
}
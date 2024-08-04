package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Ok;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;
import magma.app.compile.Splitter;

import java.util.ArrayList;

public final class NodeListRule implements Rule {
    private final String propertyName;
    private final Rule childRule;
    private final Splitter splitter;

    public NodeListRule(Splitter splitter, String propertyName, Rule childRule) {
        this.propertyName = propertyName;
        this.childRule = childRule;
        this.splitter = splitter;
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        var rootMembers = splitter.split(input);
        var children = new ArrayList<Node>();
        for (var rootMember : rootMembers) {
            var stripped = rootMember.strip();
            if (stripped.isEmpty()) continue;

            var parsed = childRule.parse(stripped);
            var valueOptional = parsed.findValue();
            if (valueOptional.isPresent()) {
                children.add(valueOptional.get());
            } else {
                return parsed;
            }
        }

        return new RuleResult<>(new Ok<>(new Node().withNodeList(propertyName, children)));
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        var childrenOptional = node.findNodeList(propertyName);
        if (childrenOptional.isEmpty()) {
            return new RuleResult<>(new Err<>(new GenerateError("Node list property '%s' was not present".formatted(propertyName), node)));
        }

        var builder = new StringBuilder();
        for (Node child : childrenOptional.get()) {
            var generated = childRule.generate(child);
            var valueOptional = generated.findValue();
            if (valueOptional.isPresent()) {
                builder.append(valueOptional.get());
            } else {
                return generated;
            }
        }

        return new RuleResult<>(new Ok<>(builder.toString()));
    }
}
package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Ok;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;
import magma.app.compile.split.Splitter;

import java.util.ArrayList;
import java.util.Iterator;

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
        Iterator<String> iterator = rootMembers.iterator();
        while (iterator.hasNext()) {
            var rootMember = iterator.next();
            var stripped = rootMember.strip();
            if (stripped.isEmpty()) continue;

            var parsed = childRule.parse(stripped);
            var valueOptional = parsed.findValue();
            if (valueOptional.isPresent()) {
                children.add(valueOptional.get());
            } else {
                return parsed.wrapErr(() -> new ParseError("Cannot create node list '" + propertyName + "'", input));
            }
        }

        return RuleResult.RuleResult(new Ok<>(new Node().withNodeList(propertyName, children)));
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        var childrenOptional = node.findNodeList(propertyName);
        if (childrenOptional.isEmpty()) {
            return RuleResult.RuleResult(Err.Err(new GenerateError("Node list property '%s' was not present".formatted(propertyName), node)));
        }

        var builder = new StringBuilder();
        Iterator<Node> iterator = childrenOptional.get().iterator();
        while (iterator.hasNext()) {
            Node child = iterator.next();
            var generated = childRule.generate(child);
            var valueOptional = generated.findValue();
            if (valueOptional.isPresent()) {
                var wasEmpty = builder.isEmpty();
                builder.append(valueOptional.get());
                if (!wasEmpty) builder.append(splitter.computeDelimiter());
            } else {
                return generated;
            }
        }

        return RuleResult.RuleResult(new Ok<>(builder.toString()));
    }
}
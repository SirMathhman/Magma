package magma.app.compile.type;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.text.Strings;
import magma.app.compile.node.Node;
import magma.app.compile.rule.Rule;

public class PrimitiveRule implements Rule<Node> {
    @Override
    public Option<Node> lex(String input) {
        var stripped = Strings.strip(input);
        if (Primitives.JavaToVariant.containsKey(stripped)) {
            return new Some<Node>(Primitives.JavaToVariant.get(stripped));
        }

        return new None<Node>();
    }
}

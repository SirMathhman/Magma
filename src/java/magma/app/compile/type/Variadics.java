package magma.app.compile.type;

import magma.app.TypeCompiler;
import magma.app.compile.node.Node;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

public class Variadics {
    public static Rule<Node> createVariadicRule() {
        return new TypeRule("variadic", new StripRule(new SuffixRule<Node>("...", new NodeRule("child", TypeCompiler::lexType))));
    }
}

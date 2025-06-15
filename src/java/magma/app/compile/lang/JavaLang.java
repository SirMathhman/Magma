package magma.app.compile.lang;

import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.LastRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;

import java.util.List;

public class JavaLang {
    public static Rule<NodeWithEverything> createJavaRootRule() {
        return CommonLang.Divide(List.of(createImportRule(), new StringRule<NodeWithEverything>("value", new MapNodeFactory())));
    }

    private static Rule<NodeWithEverything> createImportRule() {
        return new StripRule(new PrefixRule("import ", new SuffixRule(new LastRule(new StringRule<NodeWithEverything>("parent", new MapNodeFactory()), ".", new StringRule<NodeWithEverything>("child", new MapNodeFactory())), ";")));
    }
}

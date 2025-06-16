package magma.app.compile.lang;

import magma.app.compile.lang.everything.EverythingRule;
import magma.app.compile.lang.everything.EverythingRuleImpl;
import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.rule.LastRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;

import java.util.List;

public class JavaLang {
    public static EverythingRule createJavaRootRule() {
        return CommonLang.Divide(List.of(createImportRule(), getValue()));
    }

    private static EverythingRule getValue() {
        return new EverythingRuleImpl(new StringRule<>("value", new MapNodeFactory()));
    }

    private static EverythingRule createImportRule() {
        final var parent = new StringRule<>("parent", new MapNodeFactory());
        final var child = new StringRule<>("child", new MapNodeFactory());
        return new EverythingRuleImpl(new StripRule<>(new PrefixRule<>("import ", new SuffixRule<>(new LastRule<>(parent, ".", child), ";"))));
    }
}

package magma.app.compile.lang;

import magma.app.compile.lang.everything.EverythingRule;
import magma.app.compile.lang.everything.EverythingRuleImpl;
import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.LastRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.truncate.TruncateRule;

import java.util.List;

public class PlantUMLLang {
    public static EverythingRule createPlantUMLRootRule() {
        return new EverythingRuleImpl(CommonLang.Divide(List.of(createDependencyRule(), new EverythingRuleImpl(new EmptyRule<>(new MapNodeFactory())))));
    }

    private static EverythingRule createDependencyRule() {
        final var parent = new StringRule<>("parent", new MapNodeFactory());
        final var child = new StringRule<>("child", new MapNodeFactory());
        return new EverythingRuleImpl(TruncateRule.Suffix(new LastRule<>(parent, " --> ", child), "\n"));
    }
}

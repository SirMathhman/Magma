package magma.app.compile.lang;

import magma.app.compile.error.DefaultCompileResultFactory;
import magma.app.compile.lang.build.FactoryRuleBuilder;
import magma.app.compile.lang.build.RuleBuilder;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.Rule;

import java.util.List;

public class CommonLang {
    private static final RuleBuilder BUILDER = new FactoryRuleBuilder(DefaultCompileResultFactory.create());

    public static Rule<NodeWithEverything> createPlantUMLRootRule() {
        return BUILDER.NodeList(List.of(createDependencyRule(), BUILDER.Empty()));
    }

    private static Rule<NodeWithEverything> createDependencyRule() {
        final var parent = BUILDER.String("parent");
        final var child = BUILDER.String("child");
        return BUILDER.Suffix(BUILDER.Last(parent, " --> ", child), "\n");
    }

    public static Rule<NodeWithEverything> createJavaRootRule() {
        return BUILDER.NodeList(List.of(createImportRule(), BUILDER.String("value")));
    }

    private static Rule<NodeWithEverything> createImportRule() {
        final var parent = BUILDER.String("parent");
        final var child = BUILDER.String("child");
        return BUILDER.Strip(BUILDER.Prefix(BUILDER.Suffix(BUILDER.Last(parent, ".", child), ";")));
    }
}

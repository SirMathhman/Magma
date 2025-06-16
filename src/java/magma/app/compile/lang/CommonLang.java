package magma.app.compile.lang;

import magma.app.compile.error.DefaultCompileResultFactory;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.NodeResult;
import magma.app.compile.lang.build.FactoryRuleBuilder;
import magma.app.compile.lang.build.RuleBuilder;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.Rule;

import java.util.List;

public class CommonLang {
    private static final RuleBuilder<FormattedError> BUILDER = new FactoryRuleBuilder<>(DefaultCompileResultFactory.create());

    public static Rule<NodeWithEverything, FormattedError, NodeResult<NodeWithEverything, FormattedError>> createPlantUMLRootRule() {
        return BUILDER.NodeList(List.of(createDependencyRule(), BUILDER.Empty()));
    }

    private static Rule<NodeWithEverything, FormattedError, NodeResult<NodeWithEverything, FormattedError>> createDependencyRule() {
        final var parent = BUILDER.String("parent");
        final var child = BUILDER.String("child");
        return BUILDER.Suffix(BUILDER.Last(parent, " --> ", child), "\n");
    }

    public static Rule<NodeWithEverything, FormattedError, NodeResult<NodeWithEverything, FormattedError>> createJavaRootRule() {
        return BUILDER.NodeList(List.of(createImportRule(), BUILDER.String("value")));
    }

    private static Rule<NodeWithEverything, FormattedError, NodeResult<NodeWithEverything, FormattedError>> createImportRule() {
        final var parent = BUILDER.String("parent");
        final var child = BUILDER.String("child");
        return BUILDER.Strip(BUILDER.Prefix(BUILDER.Suffix(BUILDER.Last(parent, ".", child), ";")));
    }
}

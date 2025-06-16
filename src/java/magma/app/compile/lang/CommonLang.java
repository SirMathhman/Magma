package magma.app.compile.lang;

import magma.app.compile.error.DefaultCompileResultFactory;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.StringResult;
import magma.app.compile.lang.build.FactoryRuleBuilder;
import magma.app.compile.lang.build.RuleBuilder;
import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.Rule;

import java.util.List;

public class CommonLang {
    private static final RuleBuilder<NodeWithEverything, NodeResult<NodeWithEverything, FormattedError, StringResult<FormattedError>>, StringResult<FormattedError>> BUILDER = new FactoryRuleBuilder<>(DefaultCompileResultFactory.create(), new MapNodeFactory());

    public static Rule<NodeWithEverything, NodeResult<NodeWithEverything, FormattedError, StringResult<FormattedError>>, StringResult<FormattedError>> createPlantUMLRootRule() {
        return BUILDER.NodeList(List.of(createDependencyRule(), BUILDER.Empty()));
    }

    private static Rule<NodeWithEverything, NodeResult<NodeWithEverything, FormattedError, StringResult<FormattedError>>, StringResult<FormattedError>> createDependencyRule() {
        final var parent = BUILDER.String("parent");
        final var child = BUILDER.String("child");
        return BUILDER.Suffix(BUILDER.Last(parent, " --> ", child), "\n");
    }

    public static Rule<NodeWithEverything, NodeResult<NodeWithEverything, FormattedError, StringResult<FormattedError>>, StringResult<FormattedError>> createJavaRootRule() {
        return BUILDER.NodeList(List.of(createImportRule(), BUILDER.String("value")));
    }

    private static Rule<NodeWithEverything, NodeResult<NodeWithEverything, FormattedError, StringResult<FormattedError>>, StringResult<FormattedError>> createImportRule() {
        final var parent = BUILDER.String("parent");
        final var child = BUILDER.String("child");
        return BUILDER.Strip(BUILDER.Prefix(BUILDER.Suffix(BUILDER.Last(parent, ".", child), ";")));
    }
}

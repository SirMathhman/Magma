package magma.app.compile.lang.build;

import magma.app.compile.error.DefaultCompileResultFactory;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.StringResult;
import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.Rule;

public class RuleFactories {
    public static RuleFactory<Rule<NodeWithEverything, NodeResult<NodeWithEverything, FormattedError, StringResult<FormattedError>>, StringResult<FormattedError>>> create() {
        return new CompositeRuleFactory<>(new MapNodeFactory(), DefaultCompileResultFactory.create());
    }
}

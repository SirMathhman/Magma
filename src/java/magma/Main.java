package magma;

import magma.app.Application;
import magma.app.compile.RuleCompiler;
import magma.app.compile.error.DefaultCompileResultFactory;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.StringResult;
import magma.app.compile.lang.JavaRootRuleFactory;
import magma.app.compile.lang.PlantUMLRootRuleFactory;
import magma.app.compile.lang.build.FactoryRuleBuilder;
import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.Rule;

@SuppressWarnings("ClassWithTooManyTransitiveDependencies")
public class Main {
    public static void main(String[] args) {
        var builder = new FactoryRuleBuilder<>(new MapNodeFactory(), DefaultCompileResultFactory.create());
        new Application(new RuleCompiler(new JavaRootRuleFactory<>(builder).create(), new PlantUMLRootRuleFactory<Rule<NodeWithEverything, NodeResult<NodeWithEverything, FormattedError, StringResult<FormattedError>>, StringResult<FormattedError>>>(builder).create())).run()
                .ifPresent(error -> System.err.println(error.display()));
    }
}

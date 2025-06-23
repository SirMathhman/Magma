package magma.app.compile;

import magma.api.error.list.ErrorSequence;
import magma.app.compile.context.Context;
import magma.app.compile.context.ContextErrorResultFactory;
import magma.app.compile.context.SimpleContextFactory;
import magma.app.compile.error.CompileErrorFactory;
import magma.app.compile.error.ErrorFactory;
import magma.app.compile.error.FormattedError;
import magma.app.compile.factory.ResultFactory;
import magma.app.compile.lang.JavaLang;
import magma.app.compile.lang.PlantUMLJavaLang;
import magma.app.compile.node.map.MapNodeFactory;
import magma.app.compile.node.property.CompoundNode;
import magma.app.compile.node.property.NodeFactory;
import magma.app.compile.node.result.NodeResult;
import magma.app.compile.string.StringResult;

public class CompilerBuilder {
    private CompilerBuilder() {
    }

    public static Compiler createCompiler() {
        final NodeFactory<CompoundNode> nodeFactory = new MapNodeFactory();
        final ErrorFactory<Context, FormattedError, ErrorSequence<FormattedError>> errorFactory = new CompileErrorFactory();
        final ResultFactory<CompoundNode, NodeResult<CompoundNode, FormattedError>, StringResult<FormattedError>, ErrorSequence<FormattedError>> resultsFactory = new ContextErrorResultFactory<>(
                new SimpleContextFactory<>(),
                errorFactory);

        return new RuleCompiler(new JavaLang<>(nodeFactory, resultsFactory),
                new PlantUMLJavaLang<>(nodeFactory, resultsFactory));
    }
}
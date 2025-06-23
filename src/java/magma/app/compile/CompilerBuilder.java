package magma.app.compile;

import magma.api.error.list.ErrorSequence;
import magma.app.compile.context.ContextErrorResultFactory;
import magma.app.compile.context.SimpleContextFactory;
import magma.app.compile.error.CompileErrorFactory;
import magma.app.compile.error.FormattedError;
import magma.app.compile.lang.JavaLang;
import magma.app.compile.lang.PlantUMLJavaLang;
import magma.app.compile.node.map.MapNodeFactory;
import magma.app.compile.node.property.CompoundNode;

public class CompilerBuilder {
    private CompilerBuilder() {
    }

    public static Compiler createCompiler() {
        final var nodeFactory = new MapNodeFactory();
        final var resultsFactory = new ContextErrorResultFactory<CompoundNode, FormattedError, ErrorSequence<FormattedError>>(
                new SimpleContextFactory<>(), new CompileErrorFactory());

        return new RuleCompiler(new JavaLang<>(nodeFactory, resultsFactory),
                new PlantUMLJavaLang<>(nodeFactory, resultsFactory));
    }
}
package magma;

import magma.api.error.list.ErrorSequence;
import magma.api.io.PathLikes;
import magma.app.Application;
import magma.app.CompileApplication;
import magma.app.compile.Compiler;
import magma.app.compile.RuleCompiler;
import magma.app.compile.error.FormattedError;
import magma.app.compile.factory.CompileErrorFactory;
import magma.app.compile.factory.CompileErrorResultFactory;
import magma.app.compile.factory.ResultFactory;
import magma.app.compile.factory.SimpleContextFactory;
import magma.app.compile.lang.JavaLang;
import magma.app.compile.lang.PlantUMLJavaLang;
import magma.app.compile.node.EverythingNode;
import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.result.NodeResult;
import magma.app.compile.string.StringResult;
import magma.app.io.sources.PathSources;
import magma.app.io.sources.Sources;
import magma.app.io.targets.PathTargets;
import magma.app.io.targets.Targets;

class Main {
    public static final NodeFactory<EverythingNode> NODE_FACTORY = new MapNodeFactory();
    public static final ResultFactory<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>, ErrorSequence<FormattedError>> RESULTS_FACTORY = new CompileErrorResultFactory<>(
            new SimpleContextFactory<>(),
            new CompileErrorFactory());

    private Main() {
    }

    public static void main(final String[] args) {
        final var rootDirectory = PathLikes.get(".", "src", "java");
        final Sources sources = new PathSources(rootDirectory);
        final Compiler compiler = new RuleCompiler(new JavaLang<>(Main.NODE_FACTORY, Main.RESULTS_FACTORY),
                new PlantUMLJavaLang<>(Main.NODE_FACTORY, Main.RESULTS_FACTORY));
        final Targets targets = new PathTargets(PathLikes.get(".", "diagram.puml"));

        final Application application = new CompileApplication(sources, compiler, targets);
        application.run()
                .ifPresent(error -> System.err.println(error.display()));
    }
}

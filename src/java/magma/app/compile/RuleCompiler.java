package magma.app.compile;

import magma.api.error.list.ErrorSequence;
import magma.api.list.ListLikes;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.divide.Divide;
import magma.app.compile.error.FormattedError;
import magma.app.compile.factory.CompileErrorFactory;
import magma.app.compile.factory.CompileErrorResultFactory;
import magma.app.compile.factory.ResultFactory;
import magma.app.compile.factory.SimpleContextFactory;
import magma.app.compile.lang.JavaLang;
import magma.app.compile.lang.PlantUMLJavaLang;
import magma.app.compile.lang.RuleFactory;
import magma.app.compile.node.EverythingNode;
import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.result.NodeErr;
import magma.app.compile.node.result.NodeOk;
import magma.app.compile.node.result.NodeResult;
import magma.app.compile.rule.Rule;
import magma.app.compile.string.StringErr;
import magma.app.compile.string.StringOk;
import magma.app.compile.string.StringResult;

import java.util.Map;

public class RuleCompiler implements Compiler {
    private static final NodeFactory<EverythingNode> NODE_FACTORY = new MapNodeFactory();
    private static final ResultFactory<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>, ErrorSequence<FormattedError>> RESULTS_FACTORY = new CompileErrorResultFactory<>(
            new SimpleContextFactory<>(),
            new CompileErrorFactory());

    private final RuleFactory<Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>>> JAVA_LANG = new JavaLang<>(
            RuleCompiler.NODE_FACTORY,
            RuleCompiler.RESULTS_FACTORY);

    private final RuleFactory<Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>>> PLANT_UML_LANG = new PlantUMLJavaLang<>(
            RuleCompiler.NODE_FACTORY,
            RuleCompiler.RESULTS_FACTORY);

    private static boolean isFunctionalInterface(final String destination) {
        return ListLikes.of("Consumer", "Function", "Supplier")
                .contains(destination);
    }

    private StringResult<FormattedError> compileEntry(final Map<String, String> inputs) {
        StringResult<FormattedError> maybeCompiled = new StringOk<>();
        for (final var source : inputs.entrySet()) {
            final var name = source.getKey();
            final var input = source.getValue();

            maybeCompiled = maybeCompiled.tryAppendResult(() -> compile(input, name));
        }

        return maybeCompiled;
    }

    private StringResult<FormattedError> compile(final CharSequence input, final String name) {
        final var segments = Divide.divide(input);
        return segments.stream()
                .<StringResult<FormattedError>>reduce(new StringOk<>(),
                        (output, segment) -> getStringResult(name, output, segment),
                        (_, next) -> next)
                .prepend("class " + name + System.lineSeparator());
    }

    private StringResult<FormattedError> getStringResult(final String name, final StringResult<FormattedError> output, final String segment) {
        final var tree = JAVA_LANG.create()
                .lex(segment);

        final var generated = (Result<Option<StringResult<FormattedError>>, FormattedError>) switch (tree) {
            case NodeErr(final var error1) -> new Err<>(error1);
            case NodeOk(final EverythingNode value1) -> new Ok<>(transformAndGenerate(name, value1));
        };

        return switch (generated) {
            case Err<Option<StringResult<FormattedError>>, FormattedError>(final var error) -> new StringErr<>(error);
            case Ok<Option<StringResult<FormattedError>>, FormattedError>(final var value) ->
                    value instanceof Some(final var result) ? output.appendResult(result) : output;
        };
    }

    private Option<StringResult<FormattedError>> transformAndGenerate(final String name, final EverythingNode destination) {
        final var node = destination.withString("source", name);
        final var destination1 = node.findString("destination")
                .orElse("");

        if (RuleCompiler.isFunctionalInterface(destination1))
            return new None<>();

        if (node.is("import"))
            return new Some<>(generate(node.retype("dependency")));
        else
            return new None<>();
    }

    private StringResult<FormattedError> generate(final EverythingNode node) {
        return PLANT_UML_LANG.create()
                .generate(node);
    }

    @Override
    public Result<String, FormattedError> compile(final Map<String, String> inputs) {
        return switch (compileEntry(inputs)) {
            case StringErr(final var error) -> new Err<>(error);
            case StringOk(final var value) -> new Ok<>(value);
        };
    }
}

package magma.app.compile.lang;

import magma.api.error.list.ErrorSequence;
import magma.api.list.ListLikes;
import magma.app.compile.error.FormattedError;
import magma.app.compile.factory.CompileErrorFactory;
import magma.app.compile.factory.CompileErrorResultFactory;
import magma.app.compile.factory.ResultFactory;
import magma.app.compile.factory.SimpleContextFactory;
import magma.app.compile.node.EverythingNode;
import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.result.NodeResult;
import magma.app.compile.rule.InfixRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;
import magma.app.compile.string.StringResult;

public class Lang {
    public static final String SEPARATOR = System.lineSeparator();
    private static final ResultFactory<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>, ErrorSequence<FormattedError>> RESULT_FACTORY = new CompileErrorResultFactory<>(
            new SimpleContextFactory<>(),
            new CompileErrorFactory());
    private static final NodeFactory<EverythingNode> NODE_FACTORY = new MapNodeFactory();

    private Lang() {
    }

    public static Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createRootSegmentRule() {
        return new OrRule<>(ListLikes.of(Lang.createImportRule("package"),
                Lang.createImportRule("import"),
                Lang.createStructureRule("record"),
                Lang.createStructureRule("interface"),
                Lang.createStructureRule("class")), Lang.RESULT_FACTORY);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createStructureRule(final String infix) {
        return new InfixRule<>(new StringRule<>("before-keyword", Lang.NODE_FACTORY, Lang.RESULT_FACTORY),
                infix,
                new StringRule<>("after-keyword", Lang.NODE_FACTORY, Lang.RESULT_FACTORY),
                Lang.RESULT_FACTORY);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createImportRule(final String type) {
        final var destination = new StringRule<>("destination", Lang.NODE_FACTORY, Lang.RESULT_FACTORY);
        final var withParent = new InfixRule<>(new StringRule<>("parent", Lang.NODE_FACTORY, Lang.RESULT_FACTORY),
                ".",
                destination,
                Lang.RESULT_FACTORY);
        final var parent = new OrRule<>(ListLikes.of(withParent,
                new StringRule<>("value", Lang.NODE_FACTORY, Lang.RESULT_FACTORY)), Lang.RESULT_FACTORY);

        return new TypeRule<>(type,
                new StripRule<>(new PrefixRule<>(type + " ",
                        new SuffixRule<>(parent, ";", Lang.RESULT_FACTORY),
                        Lang.RESULT_FACTORY)),
                Lang.RESULT_FACTORY);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createPlaceholderRule() {
        return new TypeRule<>("placeholder",
                new StringRule<>("value", Lang.NODE_FACTORY, Lang.RESULT_FACTORY),
                Lang.RESULT_FACTORY);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createDependencyRule() {
        return new TypeRule<>("dependency",
                new SuffixRule<>(new InfixRule<>(new StringRule<>("source", Lang.NODE_FACTORY, Lang.RESULT_FACTORY),
                        " --> ",
                        new StringRule<>("destination", Lang.NODE_FACTORY, Lang.RESULT_FACTORY),
                        Lang.RESULT_FACTORY), Lang.SEPARATOR, Lang.RESULT_FACTORY),
                Lang.RESULT_FACTORY);
    }

    public static Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createPlantUMLRootSegmentRule() {
        return new OrRule<>(ListLikes.of(Lang.createDependencyRule(), Lang.createPlaceholderRule()),
                Lang.RESULT_FACTORY);
    }
}

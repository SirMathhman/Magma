package magma.compile;

import magma.api.error.ErrorSequence;
import magma.api.error.FormattedError;
import magma.api.list.ListLikes;
import magma.factory.CompileErrorFactory;
import magma.factory.CompileErrorResultFactory;
import magma.factory.ResultFactory;
import magma.factory.SimpleContextFactory;
import magma.node.EverythingNode;
import magma.node.MapNodeFactory;
import magma.node.NodeFactory;
import magma.node.result.NodeResult;
import magma.rule.InfixRule;
import magma.rule.OrRule;
import magma.rule.PrefixRule;
import magma.rule.Rule;
import magma.rule.StringRule;
import magma.rule.StripRule;
import magma.rule.SuffixRule;
import magma.rule.TypeRule;
import magma.string.StringResult;

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

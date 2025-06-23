package magma.app.compile.lang;

import magma.api.error.list.ErrorSequence;
import magma.api.list.ListLikes;
import magma.app.compile.factory.ResultFactory;
import magma.app.compile.node.DisplayNode;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.StringNode;
import magma.app.compile.node.TypedNode;
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

public class Lang<EverythingNode extends DisplayNode & StringNode<EverythingNode> & TypedNode<EverythingNode>, FormattedError> {
    private final ResultFactory<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>, ErrorSequence<FormattedError>> resultFactory;
    private final NodeFactory<EverythingNode> nodeFactory;

    public Lang(final NodeFactory<EverythingNode> nodeFactory, final ResultFactory<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>, ErrorSequence<FormattedError>> resultFactory) {
        this.nodeFactory = nodeFactory;
        this.resultFactory = resultFactory;
    }

    public Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createRootSegmentRule() {
        return new OrRule<>(ListLikes.of(createImportRule("package"),
                createImportRule("import"),
                createStructureRule("record"),
                createStructureRule("interface"), createStructureRule("class")), resultFactory);
    }

    private Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createStructureRule(final String infix) {
        return new InfixRule<>(new StringRule<>("before-keyword", nodeFactory, resultFactory),
                infix,
                new StringRule<>("after-keyword", nodeFactory, resultFactory),
                resultFactory);
    }

    private Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createImportRule(final String type) {
        final var destination = new StringRule<>("destination", nodeFactory, resultFactory);
        final var withParent = new InfixRule<>(new StringRule<>("parent", nodeFactory, resultFactory),
                ".", destination, resultFactory);
        final var parent = new OrRule<>(ListLikes.of(withParent, new StringRule<>("value", nodeFactory, resultFactory)),
                resultFactory);

        return new TypeRule<>(type,
                new StripRule<>(new PrefixRule<>(type + " ",
                        new SuffixRule<>(parent, ";", resultFactory),
                        resultFactory)), resultFactory);
    }

    private Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createPlaceholderRule() {
        return new TypeRule<>("placeholder", new StringRule<>("value", nodeFactory, resultFactory), resultFactory);
    }

    private Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createDependencyRule() {
        return new TypeRule<>("dependency",
                new SuffixRule<>(new InfixRule<>(new StringRule<>("source", nodeFactory, resultFactory),
                        " --> ",
                        new StringRule<>("destination", nodeFactory, resultFactory),
                        resultFactory), System.lineSeparator(), resultFactory),
                resultFactory);
    }

    public Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createPlantUMLRootSegmentRule() {
        return new OrRule<>(ListLikes.of(createDependencyRule(), createPlaceholderRule()), resultFactory);
    }
}

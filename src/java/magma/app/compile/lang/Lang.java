package magma.app.compile.lang;

import magma.api.error.list.ErrorSequence;
import magma.api.list.ListLikes;
import magma.app.compile.error.FormattedError;
import magma.app.compile.factory.ResultFactory;
import magma.app.compile.node.EverythingNode;
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
    private final ResultFactory<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>, ErrorSequence<FormattedError>> resultFactory;
    private final NodeFactory<EverythingNode> nodeFactory;

    public Lang(final NodeFactory<EverythingNode> nodeFactory, final ResultFactory<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>, ErrorSequence<FormattedError>> resultFactory) {
        this.nodeFactory = nodeFactory;
        this.resultFactory = resultFactory;
    }

    public Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createRootSegmentRule() {
        return new OrRule<>(ListLikes.of(this.createImportRule("package"),
                this.createImportRule("import"),
                this.createStructureRule("record"),
                this.createStructureRule("interface"), this.createStructureRule("class")), this.resultFactory);
    }

    private Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createStructureRule(final String infix) {
        return new InfixRule<>(new StringRule<>("before-keyword", this.nodeFactory, this.resultFactory),
                infix,
                new StringRule<>("after-keyword", this.nodeFactory, this.resultFactory),
                this.resultFactory);
    }

    private Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createImportRule(final String type) {
        final var destination = new StringRule<>("destination", this.nodeFactory, this.resultFactory);
        final var withParent = new InfixRule<>(new StringRule<>("parent", this.nodeFactory, this.resultFactory),
                ".", destination, this.resultFactory);
        final var parent = new OrRule<>(ListLikes.of(withParent,
                new StringRule<>("value", this.nodeFactory, this.resultFactory)), this.resultFactory);

        return new TypeRule<>(type,
                new StripRule<>(new PrefixRule<>(type + " ",
                        new SuffixRule<>(parent, ";", this.resultFactory),
                        this.resultFactory)), this.resultFactory);
    }

    private Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createPlaceholderRule() {
        return new TypeRule<>("placeholder",
                new StringRule<>("value", this.nodeFactory, this.resultFactory),
                this.resultFactory);
    }

    private Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createDependencyRule() {
        return new TypeRule<>("dependency",
                new SuffixRule<>(new InfixRule<>(new StringRule<>("source", this.nodeFactory, this.resultFactory),
                        " --> ",
                        new StringRule<>("destination", this.nodeFactory, this.resultFactory),
                        this.resultFactory), System.lineSeparator(), this.resultFactory),
                this.resultFactory);
    }

    public Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createPlantUMLRootSegmentRule() {
        return new OrRule<>(ListLikes.of(this.createDependencyRule(), this.createPlaceholderRule()),
                this.resultFactory);
    }
}

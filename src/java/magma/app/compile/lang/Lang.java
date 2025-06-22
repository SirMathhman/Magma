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
    private final ResultFactory<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>, ErrorSequence<FormattedError>> RESULT_FACTORY = new CompileErrorResultFactory<>(
            new SimpleContextFactory<>(),
            new CompileErrorFactory());
    private final NodeFactory<EverythingNode> NODE_FACTORY = new MapNodeFactory();

    public Lang() {
    }

    public Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createRootSegmentRule() {
        return new OrRule<>(ListLikes.of(this.createImportRule("package"),
                this.createImportRule("import"),
                this.createStructureRule("record"),
                this.createStructureRule("interface"),
                this.createStructureRule("class")), this.RESULT_FACTORY);
    }

    private Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createStructureRule(final String infix) {
        return new InfixRule<>(new StringRule<>("before-keyword", this.NODE_FACTORY, this.RESULT_FACTORY),
                infix, new StringRule<>("after-keyword", this.NODE_FACTORY, this.RESULT_FACTORY), this.RESULT_FACTORY);
    }

    private Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createImportRule(final String type) {
        final var destination = new StringRule<>("destination", this.NODE_FACTORY, this.RESULT_FACTORY);
        final var withParent = new InfixRule<>(new StringRule<>("parent", this.NODE_FACTORY, this.RESULT_FACTORY),
                ".",
                destination, this.RESULT_FACTORY);
        final var parent = new OrRule<>(ListLikes.of(withParent,
                new StringRule<>("value", this.NODE_FACTORY, this.RESULT_FACTORY)), this.RESULT_FACTORY);

        return new TypeRule<>(type,
                new StripRule<>(new PrefixRule<>(type + " ",
                        new SuffixRule<>(parent, ";", this.RESULT_FACTORY),
                        this.RESULT_FACTORY)), this.RESULT_FACTORY);
    }

    private Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createPlaceholderRule() {
        return new TypeRule<>("placeholder",
                new StringRule<>("value", this.NODE_FACTORY, this.RESULT_FACTORY),
                this.RESULT_FACTORY);
    }

    private Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createDependencyRule() {
        return new TypeRule<>("dependency",
                new SuffixRule<>(new InfixRule<>(new StringRule<>("source", this.NODE_FACTORY, this.RESULT_FACTORY),
                        " --> ",
                        new StringRule<>("destination", this.NODE_FACTORY, this.RESULT_FACTORY),
                        this.RESULT_FACTORY), System.lineSeparator(), this.RESULT_FACTORY),
                this.RESULT_FACTORY);
    }

    public Rule<EverythingNode, NodeResult<EverythingNode, FormattedError>, StringResult<FormattedError>> createPlantUMLRootSegmentRule() {
        return new OrRule<>(ListLikes.of(this.createDependencyRule(), this.createPlaceholderRule()),
                this.RESULT_FACTORY);
    }
}

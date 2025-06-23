package magma.app.compile.lang;

import magma.api.error.list.ErrorSequence;
import magma.api.list.ListLikes;
import magma.app.compile.factory.ResultFactory;
import magma.app.compile.node.property.CompoundNode;
import magma.app.compile.node.property.NodeFactory;
import magma.app.compile.node.result.NodeResult;
import magma.app.compile.rule.Rule;
import magma.app.compile.string.StringResult;

public class PlantUMLJavaLang<Error> extends Lang<Error> implements RuleFactory<Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>>> {
    public PlantUMLJavaLang(final NodeFactory<CompoundNode> nodeFactory, final ResultFactory<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>, ErrorSequence<Error>> resultFactory) {
        super(resultFactory, nodeFactory);
    }

    private Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>> createPlaceholderRule() {
        return Type("placeholder", String("value"));
    }

    private Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>> createDependencyRule() {
        return Type("dependency",
                Suffix(Infix(String("destination"), " --> ", String("source")), System.lineSeparator()));
    }

    @Override
    public Rule<CompoundNode, NodeResult<CompoundNode, Error>, StringResult<Error>> create() {
        return Or(ListLikes.of(createDependencyRule(), createPlaceholderRule()));
    }
}

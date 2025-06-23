package magma.app.compile.lang;

import magma.api.error.list.ErrorSequence;
import magma.api.list.ListLikes;
import magma.app.compile.factory.ResultFactory;
import magma.app.compile.node.DisplayNode;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.StringNode;
import magma.app.compile.node.TypedNode;
import magma.app.compile.node.result.NodeResult;
import magma.app.compile.rule.Rule;
import magma.app.compile.string.StringResult;

public class PlantUMLJavaLang<Node extends DisplayNode & StringNode<Node> & TypedNode<Node>, Error> extends Lang<Node, Error, Rule<Node, NodeResult<Node, Error>, StringResult<Error>>> implements
        RuleFactory<Rule<Node, NodeResult<Node, Error>, StringResult<Error>>> {
    public PlantUMLJavaLang(final NodeFactory<Node> nodeFactory, final ResultFactory<Node, NodeResult<Node, Error>, StringResult<Error>, ErrorSequence<Error>> resultFactory) {
        super(resultFactory, nodeFactory);
    }

    private Rule<Node, NodeResult<Node, Error>, StringResult<Error>> createPlaceholderRule() {
        return Type("placeholder", String("value"));
    }

    private Rule<Node, NodeResult<Node, Error>, StringResult<Error>> createDependencyRule() {
        return Type("dependency",
                Suffix(Infix(String("destination"), " --> ", String("source")), System.lineSeparator()));
    }

    @Override
    public Rule<Node, NodeResult<Node, Error>, StringResult<Error>> create() {
        return Or(ListLikes.of(createDependencyRule(), createPlaceholderRule()));
    }
}

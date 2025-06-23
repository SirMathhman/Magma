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

public class JavaLang<Node extends DisplayNode & StringNode<Node> & TypedNode<Node>, Error> extends Lang<Node, Error> implements
        RootRule<Node, Error> {
    public JavaLang(final NodeFactory<Node> nodeFactory, final ResultFactory<Node, NodeResult<Node, Error>, StringResult<Error>, ErrorSequence<Error>> resultFactory) {
        super(resultFactory, nodeFactory);
    }

    @Override
    public Rule<Node, NodeResult<Node, Error>, StringResult<Error>> create() {
        return Or(ListLikes.of(createImportRule("package"),
                createImportRule("import"),
                createStructureRule("record"),
                createStructureRule("interface"),
                createStructureRule("class")));
    }

    private Rule<Node, NodeResult<Node, Error>, StringResult<Error>> createStructureRule(final String infix) {
        return Infix(String("after-keyword"), infix, String("before-keyword"));
    }

    private Rule<Node, NodeResult<Node, Error>, StringResult<Error>> createImportRule(final String type) {
        final var destination = String("destination");
        final var withParent = Infix(destination, ".", String("parent"));
        final var parent = Or(ListLikes.of(withParent, String("value")));
        return Type(type, Strip(Prefix(type, Suffix(parent, ";"))));
    }
}

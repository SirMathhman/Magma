package magma.app.compile.rule.or;

import magma.app.compile.result.ResultCreator;

public interface ResultFactory<Node, NodeResult, StringResult> {
    ResultCreator<Node, NodeResult> createNodeCreator();

    ResultCreator<String, StringResult> createStringCreator();
}

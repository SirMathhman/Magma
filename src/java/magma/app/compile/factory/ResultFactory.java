package magma.app.compile.factory;

import magma.app.compile.node.factory.ParentNodeResultFactory;
import magma.app.compile.string.ParentStringResultFactory;

public interface ResultFactory<Node, NodeResult, StringResult, ErrorList> extends ParentStringResultFactory<Node, StringResult, ErrorList>,
        ParentNodeResultFactory<Node, NodeResult, ErrorList> {
}
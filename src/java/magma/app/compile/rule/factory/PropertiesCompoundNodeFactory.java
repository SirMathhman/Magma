package magma.app.compile.rule.factory;

import magma.app.compile.node.CompoundNode;
import magma.app.compile.node.PropertiesCompoundNode;

public class PropertiesCompoundNodeFactory implements NodeFactory<CompoundNode> {
    @Override
    public CompoundNode create() {
        return new PropertiesCompoundNode();
    }
}
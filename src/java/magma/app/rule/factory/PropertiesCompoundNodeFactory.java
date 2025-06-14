package magma.app.rule.factory;

import magma.app.node.CompoundNode;
import magma.app.node.PropertiesCompoundNode;

public class PropertiesCompoundNodeFactory implements NodeFactory<CompoundNode> {
    @Override
    public CompoundNode create() {
        return new PropertiesCompoundNode();
    }
}
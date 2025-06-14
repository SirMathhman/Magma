package magma.app.compile.node;

import magma.app.compile.node.complete.CompletingProperties;

import java.util.List;

public final class PropertiesCompoundNode implements CompoundNode {
    private final Properties<CompoundNode, String> strings;
    private final Properties<CompoundNode, List<CompoundNode>> nodeLists;

    public PropertiesCompoundNode() {
        this.strings = new CompletingProperties<>(this::withStrings);
        this.nodeLists = new CompletingProperties<>(this::withNodeLists);
    }

    public PropertiesCompoundNode(Properties<CompoundNode, String> strings, Properties<CompoundNode, List<CompoundNode>> nodeLists) {
        this.nodeLists = nodeLists;
        this.strings = strings;
    }

    private CompoundNode withNodeLists(Properties<CompoundNode, List<CompoundNode>> nodeLists) {
        return new PropertiesCompoundNode(this.strings, nodeLists);
    }

    private PropertiesCompoundNode withStrings(Properties<CompoundNode, String> properties) {
        return new PropertiesCompoundNode(properties, this.nodeLists);
    }

    @Override
    public Properties<CompoundNode, String> strings() {
        return this.strings;
    }

    @Override
    public CompoundNode merge(CompoundNode other) {
        return new PropertiesCompoundNode(this.strings.merge(other.strings()), this.nodeLists.merge(other.nodeLists()));
    }

    @Override
    public Properties<CompoundNode, List<CompoundNode>> nodeLists() {
        return this.nodeLists;
    }
}
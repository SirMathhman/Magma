package magma.app.compile.node;

import magma.app.compile.node.properties.CompletingProperties;
import magma.app.compile.node.properties.Properties;
import magma.app.compile.node.properties.complete.Completer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PropertiesCompoundNode implements CompoundNode {
    private class StringCompleter implements Completer<String, CompoundNode> {
        @Override
        public CompoundNode complete(Properties<CompoundNode, String> properties) {
            return PropertiesCompoundNode.this.withStrings(properties);
        }
    }

    private class NodeListCompleter implements Completer<List<CompoundNode>, CompoundNode> {
        @Override
        public CompoundNode complete(Properties<CompoundNode, List<CompoundNode>> nodeLists1) {
            return PropertiesCompoundNode.this.withNodeLists(nodeLists1);
        }
    }

    private final Properties<CompoundNode, String> strings;
    private final Properties<CompoundNode, List<CompoundNode>> nodeLists;

    public PropertiesCompoundNode() {
        this.strings = new CompletingProperties<>(new StringCompleter());
        this.nodeLists = new CompletingProperties<>(new NodeListCompleter());
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

    @Override
    public String display() {
        final var joinedStrings = this.strings.stream().map(this::formatEntry);
        final var joinedNodeLists = this.nodeLists.stream().map(entry -> "\n\t" + entry.getKey() + ": " + entry.getValue());
        final var joined = Stream.concat(joinedStrings, joinedNodeLists).collect(Collectors.joining(", "));
        return "{" + joined + "\n}";
    }

    private String formatEntry(Map.Entry<String, String> entry) {
        return "\n\t" + entry.getKey() + ": \"" + entry.getValue() + "\"";
    }
}
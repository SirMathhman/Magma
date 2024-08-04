package magma.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record XMLNode(String name, Map<String, String> attributes, List<XMLNode> children) {
    public XMLNode(String name) {
        this(name, Collections.emptyMap(), Collections.emptyList());
    }

    public XMLNode withAttribute(String key, String value) {
        var copy = new HashMap<>(attributes);
        copy.put(key, value);
        return new XMLNode(name, copy, children);
    }

    public XMLNode withChild(XMLNode child) {
        var copy = new ArrayList<>(children);
        copy.add(child);
        return new XMLNode(name, attributes, copy);
    }

    public XMLNode withChildren(List<XMLNode> children) {
        return new XMLNode(name, attributes, children);
    }

    public String format() {
        return format(0);
    }

    public String format(int depth) {
        var indent = "\t".repeat(depth);
        var attributesString = attributes.isEmpty() ? "" : " " + formatAttributes();
        var childrenString = children.isEmpty() ? "" : "\n" + children.stream()
                .map(child -> child.format(depth + 1))
                .collect(Collectors.joining("\n")) + "\n" + indent + "<" + name + "/>";

        var maybe = children.isEmpty() ? "/" : "";
        return indent + "<" + name + attributesString + maybe + ">" + childrenString;
    }

    private String formatAttributes() {
        return attributes.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=\"" + entry.getValue() + "\"")
                .collect(Collectors.joining(" "));
    }
}

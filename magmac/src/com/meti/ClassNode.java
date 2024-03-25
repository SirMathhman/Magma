package com.meti;

import com.meti.node.*;

import java.util.*;

public final class ClassNode {
    private final int indent;
    private final Set<String> flags;
    private final String name;
    private final Node body;

    private ClassNode(int indent, Set<String> flags, String name, Node body) {
        this.indent = indent;
        this.flags = flags;
        this.name = name;
        this.body = body;
    }

    Optional<String> render() {
        var flagString = flags().contains("public") ? "export " : "";
        String bodyString;
        if (body.apply("value").flatMap(Attribute::asString).orElseThrow().isEmpty()) {
            bodyString = "{}";
        } else {
            bodyString = "{" + body.apply("value").flatMap(Attribute::asString).orElseThrow() + "\n" + "\t".repeat(indent()) + "}";
        }
        return Optional.of(flagString + "class def " + name() + "() => " + bodyString);
    }

    public int indent() {
        return indent;
    }

    public Set<String> flags() {
        return flags;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ClassNode) obj;
        return this.indent == that.indent &&
               Objects.equals(this.flags, that.flags) &&
               Objects.equals(this.name, that.name) &&
               Objects.equals(this.body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indent, flags, name, body);
    }

    @Override
    public String toString() {
        return "ClassNode[" +
               "indent=" + indent + ", " +
               "flags=" + flags + ", " +
               "name=" + name + ", " +
               "value=" + body + ']';
    }

    public Node findBody() {
        return body;
    }

    public MapNode withBody(Node body) {
        return new MapNode("class", Map.of(
                "indent", new IntAttribute(indent),
                "flags", new StringListAttribute((List<String>) flags),
                "name", new StringAttribute(name),
                "body", new NodeAttribute(body)
        ));
    }
}
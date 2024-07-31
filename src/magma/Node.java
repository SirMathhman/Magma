package magma;

public record Node(String modifiers, String name, String content) {
    public Node() {
        this("", "", "");
    }

    public Node withModifiers(String modifiers) {
        return new Node(modifiers, name, content);
    }

    public Node withName(String name) {
        return new Node(modifiers, name, content);
    }

    public Node withContent(String content) {
        return new Node(modifiers, name, content);
    }
}
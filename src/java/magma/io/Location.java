package magma.io;

public final class Location {
    private final String namespace;
    private final String name;

    public Location(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    public String join() {
        return this.namespace + "." + this.name;
    }

    public String namespace() {
        return this.namespace;
    }

    public String name() {
        return this.name;
    }

    public boolean isNamed(String childName) {
        return this.name.equals(childName);
    }

    public Location resolveSibling(String name) {
        return new Location(this.namespace, name);
    }
}
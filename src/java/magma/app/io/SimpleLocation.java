package magma.app.io;

public final class SimpleLocation implements Location {
    private final String namespace;
    private final String name;

    public SimpleLocation(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    @Override
    public String join() {
        return this.namespace + "." + this.name;
    }

    @Override
    public boolean isNamed(String childName) {
        return this.name.equals(childName);
    }

    @Override
    public Location resolveSibling(String name) {
        return new SimpleLocation(this.namespace, name);
    }
}
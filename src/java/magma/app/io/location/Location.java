package magma.app.io.location;

public interface Location {
    String join();

    boolean isNamed(String childName);

    Location resolveSibling(String name);
}

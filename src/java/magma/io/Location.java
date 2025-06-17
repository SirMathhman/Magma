package magma.io;

public interface Location {
    String join();

    boolean isNamed(String childName);

    Location resolveSibling(String name);
}

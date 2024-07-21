package magma.app.io;

import java.util.List;
import java.util.stream.Stream;

public record InlineUnit(List<String> segments, String name, String input) implements Unit {
    @Override
    public String computeName() {
        return name;
    }

    @Override
    public Stream<String> computeNamespace() {
        return segments.stream();
    }

    @Override
    public String read() {
        return input;
    }
}

package magma;

import java.util.List;
import java.util.stream.Stream;

public record InlineUnit(List<String> segments, String name) implements CompileUnit {
    @Override
    public String computeName() {
        return name;
    }

    @Override
    public Stream<String> computeNamespace() {
        return segments.stream();
    }
}

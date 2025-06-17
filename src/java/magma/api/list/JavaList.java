package magma.api.list;

import java.util.ArrayList;
import java.util.stream.Stream;

public record JavaList<T>(java.util.List<T> elements) implements List<T> {
    public JavaList() {
        this(new ArrayList<>());
    }

    @Override
    public Stream<T> stream() {
        return this.elements.stream();
    }

    @Override
    public List<T> add(T element) {
        this.elements.add(element);
        return this;
    }

}

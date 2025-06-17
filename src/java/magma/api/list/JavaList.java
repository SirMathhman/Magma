package magma.api.list;

import java.util.ArrayList;

public record JavaList<T>(java.util.List<T> elements) implements List<T> {
    public JavaList() {
        this(new ArrayList<>());
    }

    @Override
    public Iter<T> stream() {
        return new HeadedIter<>(new RangeHead(this.elements.size())).map(this.elements::get);
    }

    @Override
    public List<T> add(T element) {
        this.elements.add(element);
        return this;
    }

}

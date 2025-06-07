package magma.util;

import java.util.ArrayList;
import java.util.function.Function;

public record JavaList<T>(java.util.List<T> elements) implements List<T> {
    public JavaList() {
        this(new ArrayList<>());
    }

    @Override
    public List<T> add(T element) {
        final var copy = new ArrayList<>(elements);
        copy.add(element);
        return new JavaList<>(copy);
    }

    @Override
    public Iterator<T> iter() {
        return createIteratorFromSize().map(elements::get);
    }

    private Iterator<Integer> createIteratorFromSize() {
        return new HeadedIterator<>(new RangeHead(elements.size()));
    }

    @Override
    public List<T> addAll(List<T> elements) {
        return elements.iter().<List<T>>fold(this, List::add);
    }

    @Override
    public Option<Tuple<List<T>, T>> popLast() {
        if (elements.isEmpty()) {
            return new None<>();
        }

        final var last = elements.removeLast();
        return new Some<>(new Tuple<>(this, last));
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public T get(int index) {
        return elements.get(index);
    }

    @Override
    public Iterator<Tuple<Integer, T>> iterWithIndex() {
        return createIteratorFromSize().map(index -> new Tuple<>(index, elements.get(index)));
    }

    @Override
    public Iterator<T> iterReversed() {
        return createIteratorFromSize()
                .map(index -> elements.size() - index - 1)
                .map(elements::get);
    }

    @Override
    public List<T> mapLast(Function<T, T> mapper) {
        if (elements.isEmpty()) {
            return this;
        }

        final var mapped = mapper.apply(elements.getLast());
        elements.set(elements.size() - 1, mapped);
        return this;
    }

}

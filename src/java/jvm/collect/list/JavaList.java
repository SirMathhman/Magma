package jvm.collect.list;

import magma.collect.list.List_;
import magma.collect.stream.Stream;
import magma.collect.stream.head.HeadedStream;
import magma.collect.stream.head.RangeHead;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.option.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public record JavaList<T>(List<T> list) implements List_<T> {
    public JavaList() {
        this(new ArrayList<>());
    }

    @Override
    public Stream<T> stream() {
        return new HeadedStream<>(new RangeHead(list.size()))
                .map(list::get);
    }

    @Override
    public List_<T> add(T element) {
        List<T> copy = toNativeCopy();
        copy.add(element);
        return new JavaList<>(copy);
    }

    @Override
    public List_<T> addAll(List_<T> others) {
        List<T> copy = toNativeCopy();
        copy.addAll(Lists.toNative(others));
        return new JavaList<>(copy);
    }

    @Override
    public Option<T> findFirst() {
        if (list.isEmpty()) return new None<>();
        return new Some<>(list.getFirst());
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public List_<T> subList(int start, int end) {
        return new JavaList<>(list.subList(start, end));
    }

    @Override
    public boolean equalsTo(List_<T> other) {
        return list.equals(Lists.toNative(other));
    }

    @Override
    public List_<T> sort(BiFunction<T, T, Integer> comparator) {
        ArrayList<T> copy = new ArrayList<>(list);
        copy.sort(comparator::apply);
        return new JavaList<>(copy);
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public Option<Tuple<T, List_<T>>> popFirst() {
        if (list.isEmpty()) return new None<>();

        T first = list.getFirst();
        List<T> elements = list.subList(1, list.size());

        return new Some<>(new Tuple<>(first, new JavaList<>(elements)));
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    private List<T> toNativeCopy() {
        return new ArrayList<>(list);
    }
}

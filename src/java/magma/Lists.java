package magma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class Lists {
    public static class JavaList<T> implements Main.List_<T> {
        private final List<T> inner;

        public JavaList() {
            this(new ArrayList<>());
        }

        public JavaList(List<T> inner) {
            this.inner = inner;
        }

        @Override
        public String toString() {
            return this.inner.toString();
        }

        @Override
        public Main.List_<T> add(T element) {
            this.inner.add(element);
            return this;
        }

        @Override
        public Main.List_<T> addAll(Main.List_<T> others) {
            others.iter().forEach(this.inner::add);
            return this;
        }

        @Override
        public Main.Iterator<T> iter() {
            return new Main.HeadedIterator<>(new Main.RangeHead(this.inner.size())).map(this.inner::get);
        }

        @Override
        public boolean isEmpty() {
            return this.inner.isEmpty();
        }

        @Override
        public int size() {
            return this.inner.size();
        }

        @Override
        public Main.List_<T> slice(int startInclusive, int endExclusive) {
            return new JavaList<>(this.inner.subList(startInclusive, endExclusive));
        }

        @Override
        public Main.Option<Main.Tuple<T, Main.List_<T>>> popFirst() {
            if (this.inner.isEmpty()) {
                return new Main.None<>();
            }

            T first = this.inner.getFirst();
            List<T> slice = this.inner.subList(1, this.inner.size());
            return new Main.Some<>(new Main.Tuple<>(first, new JavaList<>(slice)));
        }

        @Override
        public Main.Option<T> peekFirst() {
            if (this.inner.isEmpty()) {
                return new Main.None<>();
            }
            return new Main.Some<>(this.inner.getFirst());
        }

        @Override
        public T get(int index) {
            return this.inner.get(index);
        }

        @Override
        public Main.List_<T> sort(BiFunction<T, T, Integer> comparator) {
            ArrayList<T> copy = new ArrayList<>(this.inner);
            copy.sort(comparator::apply);
            return new JavaList<>(copy);
        }

        @Override
        public T last() {
            return this.inner.getLast();
        }

        @Override
        public Main.List_<T> set(int index, T element) {
            ArrayList<T> list = new ArrayList<>(this.inner);
            list.add(index, element);
            return new JavaList<>(list);
        }
    }

    public static <T> Main.List_<T> empty() {
        return new JavaList<>();
    }

    public static <T> Main.List_<T> of(T... elements) {
        return new JavaList<>(Arrays.asList(elements));
    }

    public static <T> boolean contains(Main.List_<T> lists, T element, BiFunction<T, T, Boolean> equator) {
        if (lists.isEmpty()) {
            return false;
        }
        return lists.iter().anyMatch(child -> equator.apply(child, element));
    }

    public static <T> boolean equalsTo(Main.List_<T> first, Main.List_<T> second, BiFunction<T, T, Boolean> equator) {
        if (first.size() != second.size()) {
            return false;
        }

        return new Main.HeadedIterator<>(new Main.RangeHead(first.size()))
                .allMatch(index -> equator.apply(first.get(index), second.get(index)));
    }

    public static List<String> unwrap(Main.List_<String> list) {
        return list.iter().foldWithInitial(new ArrayList<String>(), (strings, s) -> {
            strings.add(s);
            return strings;
        });
    }
}

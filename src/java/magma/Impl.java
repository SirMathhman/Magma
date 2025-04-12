package magma;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class Impl {
    private record ExceptionalIOError(IOException exception) implements Main.IOError {
        @Override
        public Main.String_ display() {
            StringWriter writer = new StringWriter();
            this.exception.printStackTrace(new PrintWriter(writer));
            return new JavaString(writer.toString());
        }
    }

    private record PathImpl(Path path) implements Main.Path_ {
        private Main.Path_ resolveSibling0(String sibling) {
            return new PathImpl(this.path.resolveSibling(sibling));
        }

        @Override
        public Main.List_<String> listNames() {
            ArrayList<String> names = new ArrayList<>();
            for (Path path1 : this.path) {
                names.add(path1.toString());
            }
            return new JavaList<>(names);
        }

        @Override
        public Main.Path_ resolveSibling(Main.String_ sibling) {
            return this.resolveSibling0(Impl.fromString(sibling));
        }
    }

    private record JavaList<T>(List<T> elements) implements Main.List_<T> {
        public JavaList() {
            this(new ArrayList<>());
        }

        @Override
        public Main.List_<T> add(T element) {
            this.elements.add(element);
            return this;
        }

        @Override
        public Main.List_<T> addAll(Main.List_<T> elements) {
            elements.iter().forEach(this::add);
            return this;
        }

        @Override
        public Main.Iterator<T> iter() {
            return new Main.HeadedIterator<>(new Main.RangeHead(this.elements.size())).map(this.elements::get);
        }

        @Override
        public Main.Option<Main.Tuple<T, Main.List_<T>>> popFirst() {
            if (this.elements.isEmpty()) {
                return new Main.None<>();
            }

            T first = this.elements.getFirst();
            List<T> slice = this.elements.subList(1, this.elements.size());
            return new Main.Some<>(new Main.Tuple<>(first, new JavaList<>(slice)));
        }

        @Override
        public T pop() {
            return this.elements.removeFirst();
        }

        @Override
        public boolean isEmpty() {
            return this.elements.isEmpty();
        }

        @Override
        public T peek() {
            return this.elements.getFirst();
        }

        @Override
        public int size() {
            return this.elements.size();
        }

        @Override
        public Main.List_<T> slice(int startInclusive, int endExclusive) {
            return new JavaList<>(this.elements.subList(startInclusive, endExclusive));
        }

        @Override
        public T get(int index) {
            return this.elements.get(index);
        }
    }

    record JavaMap<K, V>(Map<K, V> internalMap, BiFunction<K, K, Boolean> equator) implements Main.Map_<K, V> {
        public JavaMap(BiFunction<K, K, Boolean> equator1) {
            this(new HashMap<>(), equator1);
        }

        @Override
        public Main.Map_<K, V> with(K key, V value) {
            HashMap<K, V> copy = new HashMap<>(this.internalMap);
            copy.put(key, value);
            return new JavaMap<>(copy, this.equator);
        }

        @Override
        public Main.Option<V> find(K key) {
            return this.internalMap.entrySet()
                    .stream().filter(set -> this.equator.apply(set.getKey(), key))
                    .findFirst()
                    .map(Map.Entry::getValue)
                    .<Main.Option<V>>map(Main.Some::new)
                    .orElseGet(Main.None::new);
        }

        @Override
        public Main.Iterator<K> iterKeys() {
            return new JavaList<>(new ArrayList<>(this.internalMap.keySet())).iter();
        }

        @Override
        public Main.Iterator<Main.Tuple<K, V>> iter() {
            return new JavaList<>(new ArrayList<>(this.internalMap.entrySet()))
                    .iter()
                    .map(entry -> new Main.Tuple<>(entry.getKey(), entry.getValue()));
        }
    }

    public static final class JavaString implements Main.String_ {
        private final String value;

        public JavaString(String value) {
            this.value = value;
        }

        @Override
        public char[] toCharArray() {
            return this.value.toCharArray();
        }

        @Override
        public boolean equalsTo(Main.String_ other) {
            return Impl.fromString(this).equals(Impl.fromString(other));
        }

        @Override
        public Main.String_ concat(Main.String_ other) {
            return new JavaString(this.value + Impl.fromString(other));
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    static Main.Option<Main.IOError> writeString(Main.Path_ target, String output) {
        try {
            java.nio.file.Files.writeString(unwrap(target), output);
            return new Main.None<>();
        } catch (IOException e) {
            return new Main.Some<>(new ExceptionalIOError(e));
        }
    }

    private static Path unwrap(Main.Path_ path) {
        return path.listNames()
                .popFirst()
                .map(list -> list.right().iter().fold(Paths.get(list.left()), Path::resolve))
                .orElse(Paths.get("."));
    }

    static Main.Result<String, Main.IOError> readString(Main.Path_ source) {
        try {
            return new Main.Ok<>(java.nio.file.Files.readString(unwrap(source)));
        } catch (IOException e) {
            return new Main.Err<>(new ExceptionalIOError(e));
        }
    }

    public static Main.Path_ get(String first, String... elements) {
        return new PathImpl(Paths.get(first, elements));
    }

    public static <T> Main.List_<T> listEmpty() {
        return new JavaList<>();
    }

    public static <T> Main.List_<T> listOf(T... elements) {
        return new JavaList<>(Arrays.asList(elements));
    }

    public static <K, V> Main.Map_<K, V> mapEmpty(BiFunction<K, K, Boolean> equator) {
        return new JavaMap<>(equator);
    }

    public static String fromString(Main.String_ string) {
        return new String(string.toCharArray());
    }

    public static Main.String_ toString(String value) {
        return new JavaString(value);
    }
}

package magma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

class Maps {
    private record JavaMap<K, V>(Map<K, V> internal) implements Main.Map_<K, V> {
        public JavaMap() {
            this(new HashMap<>());
        }

        @Override
        public Main.Map_<K, V> with(K propertyKey, V propertyValue) {
            HashMap<K, V> copy = new HashMap<>(this.internal);
            copy.put(propertyKey, propertyValue);
            return new JavaMap<>(copy);
        }

        @Override
        public Main.Option<V> find(K propertyKey) {
            if (this.internal.containsKey(propertyKey)) {
                return new Main.Some<>(this.internal.get(propertyKey));
            }
            else {
                return new Main.None<>();
            }
        }

        @Override
        public Main.Map_<K, V> withAll(Main.Map_<K, V> other) {
            return other.iter().<Main.Map_<K, V>>foldWithInitial(this,
                    (current, entry) -> current.with(entry.left(), entry.right()));
        }

        @Override
        public Main.Iterator<Main.Tuple<K, V>> iter() {
            List<Map.Entry<K, V>> list = new ArrayList<>(this.internal.entrySet());
            return new Main.HeadedIterator<>(new Main.RangeHead(list.size()))
                    .map(list::get)
                    .map(entry -> new Main.Tuple<>(entry.getKey(), entry.getValue()));
        }

        @Override
        public Main.Iterator<K> keys() {
            return new Lists.JavaList<>(new ArrayList<K>(this.internal.keySet())).iter();
        }
    }

    public static <K, V> Main.Map_<K, V> empty() {
        return new JavaMap<>();
    }

    public static <K, V> boolean equalsTo(Main.Map_<K, V> first, Main.Map_<K, V> second, BiFunction<V, V, Boolean> equator) {
        return first.keys().concat(second.keys()).allMatch(key -> Main.Options.equalsTo(first.find(key), second.find(key), equator));
    }
}

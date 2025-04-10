package magma;

import java.util.HashMap;
import java.util.Map;

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
    }

    public static <K, V> Main.Map_<K, V> empty() {
        return new JavaMap<>();
    }
}

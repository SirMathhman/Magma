package magma.api.collect.stream;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.java.JavaOptionals;

import java.util.List;
import java.util.Optional;

public class Streams {

    public static <T> Stream<T> fromNativeList(List<T> list) {
        return new AbstractStream<T>(new Head<T>() {
            private int counter = 0;

            @Override
            public Option<T> head() {
                return JavaOptionals.fromNative(head0());
            }

            private Optional<T> head0() {
                if (counter >= list.size()) return Optional.empty();
                var next = list.get(counter);
                counter++;
                return Optional.of(next);
            }
        });
    }

    public static <T> Stream<T> fromOption(Option<T> option) {
        return new AbstractStream<>(option
                .<Head<T>>map(SingleHead::new)
                .orElseGet(EmptyHead::new));
    }

    public static Stream<Integer> from(int extent) {
        return new AbstractStream<>(new Head<>() {
            private int counter = 0;

            @Override
            public Option<Integer> head() {
                if (counter >= extent) return new None<>();
                var value = counter;
                counter++;
                return new Some<>(value);
            }
        });
    }

}
package magma.api.collect.stream;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public record JavaStream<Value>(Stream<Value> stream) implements StreamLike<Value> {
    @Override
    public <Return> StreamLike<Return> map(final Function<Value, Return> mapper) {
        return new JavaStream<>(this.stream.map(mapper));
    }

    @Override
    public <Return> StreamLike<Return> flatMap(final Function<Value, StreamLike<Return>> mapper) {
        return new JavaStream<>(this.stream.flatMap(value -> mapper.apply(value)
                .unwrap()));
    }

    @Override
    public <Collection> Collection collect(final Collector<? super Value, Collection> collector) {
        return this.stream.reduce(collector.createInitial(), collector::fold, (_, next) -> next);
    }

    @Override
    public Stream<Value> unwrap() {
        return this.stream;
    }

    @Override
    public StreamLike<Value> filter(final Predicate<Value> filter) {
        return new JavaStream<>(this.stream.filter(filter));
    }
}

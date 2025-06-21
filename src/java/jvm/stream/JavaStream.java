package jvm.stream;

import magma.app.StreamLike;

import java.util.function.Function;
import java.util.stream.Collector;
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
    public <Collection> Collection collect(final Collector<? super Value, ?, Collection> collector) {
        return this.stream.collect(collector);
    }

    @Override
    public Stream<Value> unwrap() {
        return this.stream;
    }
}

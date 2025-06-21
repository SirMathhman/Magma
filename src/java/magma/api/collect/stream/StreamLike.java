package magma.api.collect.stream;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface StreamLike<Value> {
    <Return> StreamLike<Return> map(Function<Value, Return> mapper);

    <Collection> Collection collect(Collector<? super Value, Collection> collector);

    Stream<Value> unwrap();

    StreamLike<Value> filter(Predicate<Value> filter);
}

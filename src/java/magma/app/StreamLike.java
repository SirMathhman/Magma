package magma.app;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public interface StreamLike<Value> {
    <Return> StreamLike<Return> map(Function<Value, Return> mapper);

    <Return> StreamLike<Return> flatMap(Function<Value, Stream<Return>> mapper);

    <Collection> Collection collect(Collector<? super Value, ?, Collection> collector);
}

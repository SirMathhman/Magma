package magma.app.stream;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface StreamLike<Value> {
    <Return> StreamLike<Return> map(Function<Value, Return> mapper);

    <Return> StreamLike<Return> flatMap(Function<Value, Stream<Return>> mapper);

    <Collection> Collection collect(CollectorLike<Value, Collection> collector);

    StreamLike<Value> filter(Predicate<Value> predicate);
}

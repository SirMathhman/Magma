package magma;

import magma.result.Ok;
import magma.result.Result;

public record ResultCollector<Value, Collection, Error>(
        Collector<Value, Collection> collector) implements Collector<Result<Value, Error>, Result<Collection, Error>> {
    @Override
    public Result<Collection, Error> createInitial() {
        return new Ok<>(this.collector.createInitial());
    }

    @Override
    public Result<Collection, Error> fold(final Result<Collection, Error> maybeCurrent, final Result<Value, Error> maybeElement) {
        return maybeCurrent.flatMapValue(current -> maybeElement.mapValue(element -> this.collector.fold(current,
                element)));
    }
}

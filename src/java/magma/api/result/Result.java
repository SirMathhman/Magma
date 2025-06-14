package magma.api.result;

import magma.api.Tuple;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Result<Value, Error> {
    <Return> Result<Return, Error> mapValue(Function<Value, Return> mapper);

    <Other> Result<Tuple<Value, Other>, Error> and(Supplier<Result<Other, Error>> other);

    <Return> Result<Return, Error> flatMapValue(Function<Value, Result<Return, Error>> mapper);

    <Return> Return match(Function<Value, Return> whenOk, Function<Error, Return> whenErr);
}
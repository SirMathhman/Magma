package magma.result;

import java.util.function.Function;

public interface Result<Value, Error> extends Matchable<Value, Error> {
    <Return> Result<Return, Error> mapValue(Function<Value, Return> mapper);

    <Return> Result<Value, Return> mapErr(Function<Error, Return> mapper);

    <Return> Result<Return, Error> flatMapValue(Function<Value, Result<Return, Error>> mapper);
}

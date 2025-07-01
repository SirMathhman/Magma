package magma.string.result;

import magma.result.Matchable;

public interface StringResult<Error>
        extends Matchable<String, Error>, ConcatStringResult<StringResult<Error>>,
        MappingStringResult<StringResult<Error>> {}

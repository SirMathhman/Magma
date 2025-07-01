package magma.string.result;

import java.util.function.Function;

public interface MappingStringResult<Self> {
    Self flatMap(Function<String, Self> mapper);

    Self map(Function<String, String> mapper);
}

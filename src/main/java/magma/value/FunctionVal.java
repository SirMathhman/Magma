package magma.value;

import java.util.List;

public record FunctionVal(List<String> params, String body) implements Value {}

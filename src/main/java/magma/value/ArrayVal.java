package magma.value;

import java.util.List;

public record ArrayVal(List<Value> elements) implements Value {
}

package magma.app.compile.value;

import magma.api.collect.list.Iterable;
import magma.app.compile.define.Definition;

public record Lambda(Iterable<Definition> parameters, String content) implements Value {

}

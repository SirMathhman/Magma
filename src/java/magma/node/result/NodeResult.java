package magma.node.result;

import magma.node.Node;
import magma.option.Option;

import java.util.function.Function;

public interface NodeResult {
    <Return> Option<Return> flatMap(Function<Node, Option<Return>> mapper);
}

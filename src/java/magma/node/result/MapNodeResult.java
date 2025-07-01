package magma.node.result;

import java.util.function.Function;

public interface MapNodeResult<Self, Node, Factory> {
    Self mapValue(Function<Node, Node> mapper);

    Self mapErr(String message, String context, Factory factory);

    Self flatMap(Function<Node, Self> mapper);
}

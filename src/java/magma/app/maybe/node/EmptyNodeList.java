package magma.app.maybe.node;

import magma.app.Node;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.MaybeNodeList;
import magma.app.maybe.MaybeString;
import magma.app.maybe.string.EmptyString;

import java.util.List;
import java.util.function.Function;

public class EmptyNodeList implements MaybeNodeList {
    @Override
    public MaybeNodeList add(MaybeNode node) {
        return this;
    }

    @Override
    public MaybeNodeList transform(Function<List<Node>, List<Node>> mapper) {
        return this;
    }

    @Override
    public MaybeString generate(Function<List<Node>, MaybeString> generator) {
        return new EmptyString();
    }
}

package magma.compile;

import magma.util.*;
import magma.ast.*;
public record Stack(List<Frame> frames) {
    public Option<StructureType> resolveType(String name) {
        return frames.iterReversed()
                .map(frame -> frame.resolveType(name))
                .flatMap(Iterators::fromOptional)
                .next();
    }

    public Option<Type> resolveValue(String name) {
        return frames().iterReversed()
                .map(frame -> frame.resolveValue(name))
                .flatMap(Iterators::fromOptional)
                .next()
                .map(definition -> definition.type);
    }

    public Stack enter(Frame frame) {
        return new Stack(frames.add(frame));
    }

    public Option<Tuple<Stack, Frame>> exit() {
        return frames.popLast().map(tuple -> {
            return new Tuple<>(new Stack(tuple.left), tuple.right);
        });
    }

    public Option<TypeParam> resolveTypeParam(String value) {
        return frames.iter()
                .map(frame -> frame.resolveTypeParam(value))
                .flatMap(Iterators::fromOptional)
                .next();
    }
}

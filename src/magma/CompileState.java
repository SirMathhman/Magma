package magma;

import java.util.function.Function;

final class CompileState {
    public final Stack stack;
    public List<String> structures;

    private CompileState(Stack stack, List<String> structures) {
        this.stack = stack;
        this.structures = structures;
    }

    CompileState() {
        this(new Stack(Lists.of(new RootFrame())), Lists.empty());
    }

    public CompileState mapLast(Function<Frame, Frame> mapper) {
        return new CompileState(new Stack(stack.frames().mapLast(mapper)), structures);
    }

    public CompileState addStructure(String structure) {
        return new CompileState(stack, structures.add(structure));
    }

    public CompileState enter(Frame frame) {
        return new CompileState(stack.enter(frame), structures);
    }

    public Option<Tuple<CompileState, Frame>> exit() {
        return stack.exit().map(stack -> {
            return new Tuple<>(new CompileState(stack.left, structures), stack.right);
        });
    }
}

package magma.app.compile.node;

import magma.app.compile.CompileError;
import magma.api.Error;
import magma.app.compile.NodeListResult;
import magma.app.compile.NodeResult;
import magma.app.compile.rule.StringContext;
import magma.app.compile.rule.or.OrState;

import java.util.List;

public class NodeResults {
    private record OkNodeResult<Node, Error>(Node node) implements NodeResult<Node, Error> {
        @Override
        public NodeListResult<Node, Error, NodeResult<Node, Error>> attachTo(List<Node> list) {
            list.add(this.node);
            return new PresentNodeListResult<>(list);
        }

        @Override
        public OrState<Node, Error> attachTo(OrState<Node, Error> state) {
            return state.withValue(this.node);
        }
    }

    private record ErrNodeResult<Node, E extends Error>(E error) implements NodeResult<Node, E> {
        @Override
        public NodeListResult<Node, E, NodeResult<Node, E>> attachTo(List<Node> list) {
            return new ErrNodeListResult<>(this.error);
        }

        @Override
        public OrState<Node, E> attachTo(OrState<Node, E> state) {
            return state.withError(this.error);
        }
    }

    public static <Node> NodeResult<Node, CompileError> createFromStringAndErrors(String message, String input, List<CompileError> errors) {
        return new ErrNodeResult<>(new CompileError(message, new StringContext(input), errors));
    }

    public static <Node, Error> NodeResult<Node, Error> createFromValue(Node node) {
        return new OkNodeResult<>(node);
    }

    public static <Node> NodeResult<Node, CompileError> createFromString(String message, String context) {
        return new ErrNodeResult<>(new CompileError(message, new StringContext(context)));
    }
}

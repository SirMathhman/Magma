package magma.app.compile.node;

import magma.api.Error;
import magma.app.compile.CompileError;
import magma.app.compile.Node;
import magma.app.compile.NodeListResult;
import magma.app.compile.NodeResult;
import magma.app.compile.rule.StringContext;
import magma.app.compile.rule.or.OrState;

import java.util.List;

public class NodeResults {
    private record NodeOk(Node node) implements NodeResult<Node, CompileError> {
        @Override
        public NodeListResult<Node, CompileError, NodeResult<Node, CompileError>> attachTo(List<Node> list) {
            list.add(this.node);
            return new NodeListOk(list);
        }

        @Override
        public OrState<Node, CompileError> attachTo(OrState<Node, CompileError> state) {
            return state.withValue(this.node);
        }

        @Override
        public NodeListResult<Node, CompileError, NodeResult<Node, CompileError>> findNodeList(String key) {
            return this.node.findNodeList(key);
        }
    }

    private record NodeErr<Node, E extends Error>(E error) implements NodeResult<Node, E> {
        @Override
        public NodeListResult<Node, E, NodeResult<Node, E>> attachTo(List<Node> list) {
            return new NodeListErr<>(this.error);
        }

        @Override
        public OrState<Node, E> attachTo(OrState<Node, E> state) {
            return state.withError(this.error);
        }

        @Override
        public NodeListResult<Node, E, NodeResult<Node, E>> findNodeList(String key) {
            return new NodeListErr<>(this.error);
        }
    }

    public static <Node> NodeResult<Node, CompileError> createFromStringAndErrors(String message, String input, List<CompileError> errors) {
        return new NodeErr<>(new CompileError(message, new StringContext(input), errors));
    }

    public static NodeResult<Node, CompileError> createFromValue(Node node) {
        return new NodeOk(node);
    }

    public static <Node> NodeResult<Node, CompileError> createFromString(String message, String context) {
        return new NodeErr<>(new CompileError(message, new StringContext(context)));
    }

    public static <E extends Error, Node> NodeResult<Node, E> createFromError(E error) {
        return new NodeErr<>(error);
    }
}

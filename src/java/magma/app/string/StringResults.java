package magma.app.string;

import magma.app.ApplicationError;
import magma.app.ApplicationResult;
import magma.app.CompileError;
import magma.app.DisplayableNode;
import magma.api.Error;
import magma.app.Node;
import magma.app.StringResult;
import magma.app.rule.NodeContext;
import magma.app.rule.or.OrState;

import java.util.List;

public class StringResults {
    private record ErrStringResult<E extends Error>(E error) implements StringResult<E> {
        @Override
        public OrState<String, E> attachTo(OrState<String, E> state) {
            return state.withError(this.error);
        }

        @Override
        public StringResult<E> appendString(String other) {
            return this;
        }

        @Override
        public StringResult<E> appendMaybe(StringResult<E> other) {
            return this;
        }

        @Override
        public StringResult<E> prependString(String other) {
            return this;
        }

        @Override
        public ApplicationResult toApplicationResult() {
            return new ApplicationResult.Err(new ApplicationError(this.error));
        }
    }

    private record OkStringResult<Error>(String value) implements StringResult<Error> {
        @Override
        public OrState<String, Error> attachTo(OrState<String, Error> state) {
            return state.withValue(this.value);
        }

        @Override
        public StringResult<Error> appendString(String other) {
            return createFromValue(this.value + other);
        }

        @Override
        public StringResult<Error> appendMaybe(StringResult<Error> other) {
            return other.prependString(this.value);
        }

        @Override
        public StringResult<Error> prependString(String other) {
            return createFromValue(other + this.value);
        }

        @Override
        public ApplicationResult toApplicationResult() {
            return new ApplicationResult.Ok(this.value);
        }
    }

    public static <Node extends DisplayableNode> StringResult<CompileError> createFromNodeAndErrors(String message, Node node, List<CompileError> errors) {
        return new ErrStringResult<>(new CompileError(message, new NodeContext<>(node), errors));
    }

    public static <Error> StringResult<Error> createFromValue(String value) {
        return new OkStringResult<>(value);
    }

    public static StringResult<CompileError> createFromNode(String message, Node context) {
        return new ErrStringResult<>(new CompileError(message, new NodeContext<>(context)));
    }

    public static <E extends Error> StringResult<E> createFromError(E error) {
        return new ErrStringResult<>(error);
    }
}

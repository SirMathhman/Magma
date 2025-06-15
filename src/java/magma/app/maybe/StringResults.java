package magma.app.maybe;

import magma.app.CompileError;
import magma.app.Node;
import magma.app.rule.NodeContext;
import magma.app.rule.or.OrState;

import java.util.List;
import java.util.Objects;

public class StringResults {
    private record ErrStringResult<Error>(Error error) implements StringResult<Error> {
        @Override
        public String orElse(String other) {
            return other;
        }

        @Override
        public OrState<String, Error> attachTo(OrState<String, Error> state) {
            return state.withError(this.error);
        }

        @Override
        public StringResult<Error> appendString(String other) {
            return this;
        }

        @Override
        public StringResult<Error> appendMaybe(StringResult<Error> other) {
            return this;
        }

        @Override
        public StringResult<Error> prependString(String other) {
            return this;
        }
    }

    private record OkStringResult<Error>(String value) implements StringResult<Error> {
        @Override
        public String orElse(String other) {
            return this.value;
        }

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
    }

    public static <Node> StringResult<CompileError> createFromNodeAndErrors(String message, Node node, List<CompileError> errors) {
        return new ErrStringResult<>(new CompileError(message, new NodeContext<>(node), errors));
    }

    public static <Error> StringResult<Error> createFromValue(String value) {
        return new OkStringResult<>(value);
    }

    public static StringResult<CompileError> createFromNode(String message, Node context) {
        return new ErrStringResult<>(new CompileError(message, new NodeContext<>(context)));
    }

    public static <Error> StringResult<Error> createFromError(Error error) {
        return new ErrStringResult<>(error);
    }
}

package magma.app;

import magma.ApplicationError;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ApplicationResult {
    class Err implements ApplicationResult {
        private final ApplicationError error;

        public Err(ApplicationError error) {
            this.error = error;
        }

        @Override
        public ApplicationResult append(Supplier<ApplicationResult> other) {
            return this;
        }

        @Override
        public ApplicationResult compile(Function<String, ApplicationResult> mapper) {
            return this;
        }

        @Override
        public Optional<ApplicationError> extract(Function<String, Optional<ApplicationError>> replacer) {
            return Optional.of(this.error);
        }

        @Override
        public ApplicationResult prepend(String other) {
            return this;
        }
    }

    record Ok(String input) implements ApplicationResult {
        @Override
        public ApplicationResult append(Supplier<ApplicationResult> other) {
            return other.get().prepend(this.input);
        }

        @Override
        public ApplicationResult compile(Function<String, ApplicationResult> mapper) {
            return mapper.apply(this.input);
        }

        @Override
        public Optional<ApplicationError> extract(Function<String, Optional<ApplicationError>> replacer) {
            return Optional.empty();
        }

        @Override
        public ApplicationResult prepend(String other) {
            return new Ok(other + this.input);
        }
    }

    ApplicationResult append(Supplier<ApplicationResult> other);

    ApplicationResult compile(Function<String, ApplicationResult> mapper);

    Optional<ApplicationError> extract(Function<String, Optional<ApplicationError>> replacer);

    ApplicationResult prepend(String other);
}

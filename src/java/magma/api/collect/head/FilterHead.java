package magma.api.collect.head;

import java.util.Optional;
import java.util.function.Predicate;

record FilterHead<T>(Predicate<T> predicate, Head<T> head) implements Head<T> {
    @Override
    public Optional<T> next() {
        while (true) {
            final var maybeNext = this.head.next();
            if (maybeNext.isEmpty())
                return Optional.empty();

            final var next = maybeNext.get();
            if (this.predicate.test(next))
                return Optional.of(next);
        }
    }
}

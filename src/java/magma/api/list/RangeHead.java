package magma.api.list;

import java.util.Optional;

public class RangeHead implements Head<Integer> {
    private final int length;
    private int counter = 0;

    public RangeHead(int length) {
        this.length = length;
    }

    @Override
    public Optional<Integer> next() {
        if (this.counter < this.length) {
            final var value = this.counter;
            this.counter++;
            return Optional.of(value);
        }
        else
            return Optional.empty();
    }
}

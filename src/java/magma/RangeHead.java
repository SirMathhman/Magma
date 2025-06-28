package magma;

public class RangeHead implements Head<Integer> {
    private final int length;
    private int counter = 0;

    public RangeHead(final int length) {
        this.length = length;
    }

    @Override
    public Optional<Integer> next() {
        if (this.counter < this.length) {
            final var value = this.counter;
            this.counter++;
            return new Some<>(value);
        } else
            return new None<>();
    }
}

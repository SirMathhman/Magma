package magma.util;

public class RangeHead implements Head<Integer> {
    private final int length;
    private int counter = 0;

    public RangeHead(int length) {
        this.length = length;
    }

    @Override
    public Option<Integer> next() {
        if (counter >= length) {
            return new None<>();
        }

        final var value = counter;
        counter++;
        return new Some<>(value);
    }
}

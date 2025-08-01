package magma;

/**
 * A generic tuple class that holds two values of potentially different types.
 * <p>
 * This record provides a simple way to return or pass around two related values
 * as a single unit. It's used throughout the codebase for operations that need
 * to work with or return pairs of values.
 *
 * @param <Left>  The type of the left value
 * @param <Right> The type of the right value
 * @param left    The left value
 * @param right   The right value
 */
public record Tuple<Left, Right>(Left left, Right right) {}

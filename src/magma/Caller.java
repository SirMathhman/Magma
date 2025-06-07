package magma;

/**
 * Entity that can be invoked to produce a {@link Value}.
 */
sealed interface Caller extends Generating permits Value, Construction {
}

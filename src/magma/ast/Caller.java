package magma.ast;

/**
 * Entity that can be invoked to produce a {@link Value}.
 */
public sealed interface Caller extends Generating permits Value, Construction {
}

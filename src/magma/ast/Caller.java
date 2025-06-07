package magma.ast;

import magma.util.*;
import magma.compile.*;
/**
 * Entity that can be invoked to produce a {@link Value}.
 */
public sealed interface Caller extends Generating permits Value, Construction {
}

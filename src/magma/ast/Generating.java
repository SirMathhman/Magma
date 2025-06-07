package magma.ast;

import magma.util.*;
import magma.compile.*;
/**
 * Supplies generated source code for a particular node in the AST.
 */
public interface Generating {
    String generate();
}

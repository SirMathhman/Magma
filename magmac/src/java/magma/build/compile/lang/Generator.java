package magma.build.compile.lang;

import magma.api.Tuple;
import magma.api.result.Result;
import magma.build.compile.error.Error_;
import magma.build.compile.annotate.State;
import magma.build.compile.parse.Node;

/**
 * Interface representing a generator that processes a node and its state
 * to produce a result containing a new node and state, or an error.
 */
public interface Generator {

    /**
     * Generates a result by processing the given node and state.
     *
     * @param node  the input node to process
     * @param depth the state depth to consider during processing
     * @return a Result containing a Tuple of the new Node and State if successful,
     * or an Error_ if the generation fails
     */
    Result<Tuple<Node, State>, Error_> generate(Node node, State depth);
}
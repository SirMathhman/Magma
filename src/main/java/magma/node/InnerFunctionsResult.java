package magma.node;

import java.util.List;

/**
 * Helper class to hold the result of extracting inner functions.
 *
 * @param innerFunctions The list of extracted inner functions.
 * @param updatedBody    The updated function body with inner functions removed.
 */
public record InnerFunctionsResult(List<FunctionParts> innerFunctions, String updatedBody) {
	/**
	 * Constructor.
	 *
	 * @param innerFunctions The list of extracted inner functions
	 * @param updatedBody    The updated function body
	 */
	public InnerFunctionsResult {
	}
}

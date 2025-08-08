package magma.node;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to hold the various parts of a function declaration.
 */
public class FunctionParts {
	public String functionName;
	public List<Parameter> parameters;
	public int closeParenPos;
	public int arrowPos;
	public int openBracePos;
	public int closeBracePos;
	public String returnType;
	public String functionBody;
	
	/**
	 * Creates a deep copy of this FunctionParts object.
	 *
	 * @return A new FunctionParts object with the same values
	 */
	public FunctionParts copy() {
		FunctionParts copy = new FunctionParts();
		copy.functionName = this.functionName;
		copy.parameters = new ArrayList<>(this.parameters);
		copy.closeParenPos = this.closeParenPos;
		copy.arrowPos = this.arrowPos;
		copy.openBracePos = this.openBracePos;
		copy.closeBracePos = this.closeBracePos;
		copy.returnType = this.returnType;
		copy.functionBody = this.functionBody;
		return copy;
	}
}

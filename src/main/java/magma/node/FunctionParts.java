package magma.node;

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
}

package magma;

/**
 * Interpreter provides functionality to interpret source code with a given
 * input.
 */
public class Interpreter {

	/**
	 * Interpret the given source with the provided input and produce a result
	 * string.
	 *
	 * Currently this method is a stub and will always throw an InterpretException.
	 *
	 * @param source the source code to interpret
	 * @param input  the runtime input for the program
	 * @return the result of interpretation as a String (not implemented)
	 * @throws InterpretException always thrown for now with message "Invalid
	 *                            source"
	 */
	public String interpret(String source, String input) throws InterpretException {
		throw new InterpretException("Invalid source", source);
	}
}

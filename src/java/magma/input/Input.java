package magma.input;

/**
 * Represents an input source for lexical analysis.
 * This interface abstracts different types of input sources that can be used for parsing,
 * allowing for better error handling and flexibility in input processing.
 */
public interface Input {
	/**
	 * Returns a pretty-printed representation of this input.
	 * This is useful for error reporting and debugging.
	 *
	 * @return a string representation of this input
	 */
	String prettyPrint();

	/**
	 * Gets the content of this input as a string.
	 *
	 * @return the string content of this input
	 */
	String getContent();

	/**
	 * Gets the source identifier for this input (e.g., filename, description).
	 * This is useful for error reporting.
	 *
	 * @return a string identifying the source of this input
	 */
	String getSource();

	/**
	 * Gets the start index of this input in the original source.
	 *
	 * @return the start index (0-based) of this input
	 */
	int getStartIndex();

	/**
	 * Gets the end index of this input in the original source.
	 * This is exclusive, meaning it points to the position after the last character.
	 *
	 * @return the end index (0-based) of this input
	 */
	int getEndIndex();

	/**
	 * Checks if the content starts with the specified prefix.
	 *
	 * @param prefix the prefix to check for
	 * @return true if the content starts with the prefix, false otherwise
	 */
	boolean startsWith(String prefix);

	/**
	 * Checks if the content ends with the specified suffix.
	 *
	 * @param suffix the suffix to check for
	 * @return true if the content ends with the suffix, false otherwise
	 */
	boolean endsWith(String suffix);

	/**
	 * Creates a new Input containing the content after the specified prefix.
	 * The source of the new Input will be this Input's source with " (after prefix)" appended.
	 *
	 * @param prefix the prefix to remove
	 * @return a new Input with the content after the prefix
	 * @throws IllegalArgumentException if the content does not start with the prefix
	 */
	Input afterPrefix(String prefix);

	/**
	 * Creates a new Input containing the content before the specified suffix.
	 * The source of the new Input will be this Input's source with " (before suffix)" appended.
	 *
	 * @param suffix the suffix to remove
	 * @return a new Input with the content before the suffix
	 * @throws IllegalArgumentException if the content does not end with the suffix
	 */
	Input beforeSuffix(String suffix);

	/**
	 * Finds the first occurrence of the specified infix in the content.
	 *
	 * @param infix the infix to find
	 * @return the index of the first occurrence of the infix, or -1 if not found
	 */
	int indexOf(String infix);

	/**
	 * Creates a pair of Inputs by splitting the content at the specified infix.
	 * The sources of the new Inputs will be this Input's source with " (left part)" and " (right part)" appended.
	 *
	 * @param infix the infix to split at
	 * @return an array of two Inputs, the first containing the content before the infix and the second containing the content after the infix
	 * @throws IllegalArgumentException if the infix is not found in the content
	 */
	Input[] splitAtInfix(String infix);

	/**
	 * Creates a clone of this Input.
	 *
	 * @return a new Input with the same content, source, and indices
	 */
	Input clone();

	/**
	 * Creates a new Input that is a window of this Input, extending from the start by the specified number of characters.
	 *
	 * @param length the number of characters to include in the window
	 * @return a new Input representing a window from the start of this Input
	 * @throws IllegalArgumentException if length is negative or greater than the content length
	 */
	Input window(int length);

	/**
	 * Creates a new Input that is a window of this Input, starting at the specified offset and extending for the specified length.
	 *
	 * @param offset the starting offset from the beginning of this Input
	 * @param length the number of characters to include in the window
	 * @return a new Input representing a window of this Input
	 * @throws IllegalArgumentException if offset or length is negative or if the window extends beyond the content
	 */
	Input window(int offset, int length);

	/**
	 * Creates a new Input by extending this Input at the start with the specified content.
	 *
	 * @param prefix the content to add at the start
	 * @return a new Input with the extended content
	 */
	Input extendStart(String prefix);

	/**
	 * Creates a new Input by extending this Input at the end with the specified content.
	 *
	 * @param suffix the content to add at the end
	 * @return a new Input with the extended content
	 */
	Input extendEnd(String suffix);

	/**
	 * Creates a new Input by extending this Input at the start with the content from another Input.
	 *
	 * @param prefix the Input whose content to add at the start
	 * @return a new Input with the extended content
	 */
	Input extendStart(Input prefix);

	/**
	 * Creates a new Input by extending this Input at the end with the content from another Input.
	 *
	 * @param suffix the Input whose content to add at the end
	 * @return a new Input with the extended content
	 */
	Input extendEnd(Input suffix);
}
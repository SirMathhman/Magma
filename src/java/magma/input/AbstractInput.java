package magma.input;

/**
 * An abstract base implementation of the Input interface that provides common functionality.
 * This serves as the base class for all concrete Input implementations.
 */
public abstract class AbstractInput implements Input {
	protected final String content;
	protected final String source;
	protected final int startIndex;
	protected final int endIndex;

	/**
	 * Creates a new AbstractInput with the given parameters.
	 *
	 * @param content    the string content to be parsed
	 * @param source     a description of the source (e.g., filename, "inline string")
	 * @param startIndex the start index of this input in the original source
	 * @param endIndex   the end index of this input in the original source
	 */
	protected AbstractInput(final String content, final String source, final int startIndex, final int endIndex) {
		this.content = content; this.source = source; this.startIndex = startIndex; this.endIndex = endIndex;
	}

	@Override
	public String getContent() {
		return this.content;
	}

	@Override
	public String getSource() {
		return this.source;
	}

	@Override
	public int getStartIndex() {
		return this.startIndex;
	}

	@Override
	public int getEndIndex() {
		return this.endIndex;
	}

	@Override
	public boolean startsWith(final String prefix) {
		return this.content.startsWith(prefix);
	}

	@Override
	public boolean endsWith(final String suffix) {
		return this.content.endsWith(suffix);
	}

	@Override
	public int indexOf(final String infix) {
		return this.content.indexOf(infix);
	}

	@Override
	public String prettyPrint() {
		return "\"" + this.content + "\" (source: " + this.source + ", range: " + this.startIndex + "-" + this.endIndex +
					 ")";
	}

	/**
	 * Creates a WindowInput from this input with the specified window parameters.
	 *
	 * @param offset the starting offset from the beginning of this Input
	 * @param length the number of characters to include in the window
	 * @return a new WindowInput representing a window of this Input
	 * @throws IllegalArgumentException if offset or length is negative or if the window extends beyond the content
	 */
	protected Input createWindow(int offset, int length) {
		if (offset < 0 || length < 0 || offset + length > this.content.length()) {
			throw new IllegalArgumentException("Invalid window parameters: offset=" + offset + ", length=" + length);
		}

		String windowContent = this.content.substring(offset, offset + length);
		String windowSource = this.source + " (window)"; int windowStartIndex = this.startIndex + offset;
		int windowEndIndex = windowStartIndex + length;

		return new WindowInput(windowContent, windowSource, windowStartIndex, windowEndIndex, this);
	}
}
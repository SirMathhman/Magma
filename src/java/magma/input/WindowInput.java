package magma.input;

/**
 * A concrete implementation of Input that represents a window or slice of another Input.
 * WindowInput can only be created from an existing RootInput or another WindowInput.
 */
public class WindowInput extends AbstractInput {
	private final Input parent;

	/**
	 * Creates a new WindowInput with the given parameters.
	 * This constructor is package-private to ensure WindowInputs can only be created
	 * through the appropriate factory methods in AbstractInput.
	 *
	 * @param content    the string content of this window
	 * @param source     a description of the source
	 * @param startIndex the start index of this window in the original source
	 * @param endIndex   the end index of this window in the original source
	 * @param parent     the parent Input that this window was created from
	 */
	WindowInput(final String content, final String source, final int startIndex, final int endIndex, final Input parent) {
		super(content, source, startIndex, endIndex); this.parent = parent;
	}

	/**
	 * Gets the parent Input that this window was created from.
	 *
	 * @return the parent Input
	 */
	public Input getParent() {
		return parent;
	}

	@Override
	public Input afterPrefix(final String prefix) {
		if (!this.startsWith(prefix)) throw new IllegalArgumentException("Content does not start with prefix: " + prefix);
		final String remainingContent = this.content.substring(prefix.length());
		return createWindow(prefix.length(), remainingContent.length());
	}

	@Override
	public Input beforeSuffix(final String suffix) {
		if (!this.endsWith(suffix)) throw new IllegalArgumentException("Content does not end with suffix: " + suffix);
		final String remainingContent = this.content.substring(0, this.content.length() - suffix.length());
		return createWindow(0, remainingContent.length());
	}

	@Override
	public Input[] splitAtInfix(final String infix) {
		final int infixIndex = this.indexOf(infix);
		if (-1 == infixIndex) throw new IllegalArgumentException("Infix not found in content: " + infix);

		final Input leftInput = createWindow(0, infixIndex); final Input rightInput =
				createWindow(infixIndex + infix.length(), this.content.length() - infixIndex - infix.length());

		return new Input[]{leftInput, rightInput};
	}

	@Override
	public Input clone() {
		return new WindowInput(this.content, this.source, this.startIndex, this.endIndex, this.parent);
	}

	@Override
	public Input window(int length) {
		return createWindow(0, length);
	}

	@Override
	public Input window(int offset, int length) {
		return createWindow(offset, length);
	}

	@Override
	public Input extendStart(String prefix) {
		// For WindowInput, we create a new WindowInput that extends the current one
		return new WindowInput(prefix + this.content, this.source + " (extended start)", this.startIndex - prefix.length(),
													 this.endIndex, this.parent);
	}

	@Override
	public Input extendEnd(String suffix) {
		// For WindowInput, we create a new WindowInput that extends the current one
		return new WindowInput(this.content + suffix, this.source + " (extended end)", this.startIndex,
													 this.endIndex + suffix.length(), this.parent);
	}

	@Override
	public Input extendStart(Input prefix) {
		// For WindowInput, we create a new WindowInput that extends the current one
		return new WindowInput(prefix.getContent() + this.content,
													 this.source + " (extended start with " + prefix.getSource() + ")",
													 Math.min(this.startIndex, prefix.getStartIndex()), this.endIndex, this.parent);
	}

	@Override
	public Input extendEnd(Input suffix) {
		// For WindowInput, we create a new WindowInput that extends the current one
		return new WindowInput(this.content + suffix.getContent(),
													 this.source + " (extended end with " + suffix.getSource() + ")", this.startIndex,
													 Math.max(this.endIndex, suffix.getEndIndex()), this.parent);
	}
}
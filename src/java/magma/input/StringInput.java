package magma.input;

/**
 * A concrete implementation of Input that wraps a string.
 * This is the most basic form of input for lexical analysis.
 */
public class StringInput implements Input {
	private final String content;
	private final String source;
	private final int startIndex;
	private final int endIndex;

	/**
	 * Creates a new StringInput with the given content and a default source name.
	 * Start and end indices are set to 0 and content.length() respectively.
	 *
	 * @param content the string content to be parsed
	 */
	public StringInput(final String content) {
		this(content, "string");
	}

	/**
	 * Creates a new StringInput with the given content and source name.
	 * Start and end indices are set to 0 and content.length() respectively.
	 *
	 * @param content the string content to be parsed
	 * @param source  a description of the source (e.g., filename, "inline string")
	 */
	public StringInput(final String content, final String source) {
		this(content, source, 0, content.length());
	}

	/**
	 * Creates a new StringInput with the given content, source name, and position indices.
	 *
	 * @param content    the string content to be parsed
	 * @param source     a description of the source (e.g., filename, "inline string")
	 * @param startIndex the start index of this input in the original source
	 * @param endIndex   the end index of this input in the original source
	 */
	public StringInput(final String content, final String source, final int startIndex, final int endIndex) {
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
	public Input afterPrefix(final String prefix) {
		if (!this.startsWith(prefix)) throw new IllegalArgumentException("Content does not start with prefix: " + prefix);
		final String remainingContent = this.content.substring(prefix.length());
		return new StringInput(remainingContent, this.source + " (after prefix)", this.startIndex + prefix.length(),
													 this.endIndex);
	}

	@Override
	public Input beforeSuffix(final String suffix) {
		if (!this.endsWith(suffix)) throw new IllegalArgumentException("Content does not end with suffix: " + suffix);
		final String remainingContent = this.content.substring(0, this.content.length() - suffix.length());
		return new StringInput(remainingContent, this.source + " (before suffix)", this.startIndex,
													 this.startIndex + remainingContent.length());
	}

	@Override
	public int indexOf(final String infix) {
		return this.content.indexOf(infix);
	}

	@Override
	public Input[] splitAtInfix(final String infix) {
		final int infixIndex = this.indexOf(infix);
		if (-1 == infixIndex) throw new IllegalArgumentException("Infix not found in content: " + infix);

		final String leftPart = this.content.substring(0, infixIndex);
		final String rightPart = this.content.substring(infixIndex + infix.length());

		final Input leftInput =
				new StringInput(leftPart, this.source + " (left part)", this.startIndex, this.startIndex + infixIndex);
		final Input rightInput =
				new StringInput(rightPart, this.source + " (right part)", this.startIndex + infixIndex + infix.length(),
												this.endIndex);

		return new Input[]{leftInput, rightInput};
	}

	@Override
	public String prettyPrint() {
		return "\"" + this.content + "\" (source: " + this.source + ", range: " + this.startIndex + "-" + this.endIndex +
					 ")";
	}

	@Override
	public Input clone() {
		return new StringInput(this.content, this.source, this.startIndex, this.endIndex);
	}

	@Override
	public Input window(int length) {
		if (length < 0 || length > this.content.length()) {
			throw new IllegalArgumentException("Invalid window length: " + length);
		} return new StringInput(this.content.substring(0, length), this.source + " (window)", this.startIndex,
														 this.startIndex + length);
	}

	@Override
	public Input window(int offset, int length) {
		if (offset < 0 || length < 0 || offset + length > this.content.length()) {
			throw new IllegalArgumentException("Invalid window parameters: offset=" + offset + ", length=" + length);
		} return new StringInput(this.content.substring(offset, offset + length), this.source + " (window)",
														 this.startIndex + offset, this.startIndex + offset + length);
	}

	@Override
	public Input extendStart(String prefix) {
		return new StringInput(prefix + this.content, this.source + " (extended start)", this.startIndex - prefix.length(),
													 this.endIndex);
	}

	@Override
	public Input extendEnd(String suffix) {
		return new StringInput(this.content + suffix, this.source + " (extended end)", this.startIndex,
													 this.endIndex + suffix.length());
	}

	@Override
	public Input extendStart(Input prefix) {
		return new StringInput(prefix.getContent() + this.content,
													 this.source + " (extended start with " + prefix.getSource() + ")",
													 Math.min(this.startIndex, prefix.getStartIndex()), this.endIndex);
	}

	@Override
	public Input extendEnd(Input suffix) {
		return new StringInput(this.content + suffix.getContent(),
													 this.source + " (extended end with " + suffix.getSource() + ")", this.startIndex,
													 Math.max(this.endIndex, suffix.getEndIndex()));
	}
}
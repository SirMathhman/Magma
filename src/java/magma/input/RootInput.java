package magma.input;

/**
 * A concrete implementation of Input that wraps a string as a root input.
 * This is the primary entry point for creating inputs from raw strings.
 */
public class RootInput extends AbstractInput {
	/**
	 * Creates a new RootInput with the given content and a default source name.
	 * Start and end indices are set to 0 and content.length() respectively.
	 *
	 * @param content the string content to be parsed
	 */
	public RootInput(final String content) {
		this(content, "string");
	}

	/**
	 * Creates a new RootInput with the given content and source name.
	 * Start and end indices are set to 0 and content.length() respectively.
	 *
	 * @param content the string content to be parsed
	 * @param source  a description of the source (e.g., filename, "inline string")
	 */
	public RootInput(final String content, final String source) {
		this(content, source, 0, content.length());
	}

	/**
	 * Creates a new RootInput with the given content, source name, and position indices.
	 *
	 * @param content    the string content to be parsed
	 * @param source     a description of the source (e.g., filename, "inline string")
	 * @param startIndex the start index of this input in the original source
	 * @param endIndex   the end index of this input in the original source
	 */
	public RootInput(final String content, final String source, final int startIndex, final int endIndex) {
		super(content, source, startIndex, endIndex);
	}

    /**
     * Creates a new RootInput with the given content, source location, and position indices.
     *
     * @param content    the string content to be parsed
     * @param source     a Location object identifying the source
     * @param startIndex the start index of this input in the original source
     * @param endIndex   the end index of this input in the original source
     */
    public RootInput(final String content, final Location source, final int startIndex, final int endIndex) {
        super(content, source, startIndex, endIndex);
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
		return new RootInput(this.content, this.source, this.startIndex, this.endIndex);
  }

    @Override
	public Input window(int offset, int length) {
		return createWindow(offset, length);
	}

	@Override
	public Input extendStart(String prefix) {
      Location extendedSource =
          new Location(this.source.getPackageSegments(), this.source.getName() + " (extended start)");
      return new RootInput(prefix + this.content, extendedSource, this.startIndex - prefix.length(),
												 this.endIndex);
	}

	@Override
	public Input extendEnd(String suffix) {
      Location extendedSource =
          new Location(this.source.getPackageSegments(), this.source.getName() + " (extended end)");
      return new RootInput(this.content + suffix, extendedSource, this.startIndex,
												 this.endIndex + suffix.length());
	}

	@Override
	public Input extendStart(Input prefix) {
      Location extendedSource = new Location(this.source.getPackageSegments(),
                                             this.source.getName() + " (extended start with " + prefix.getSource() + ")"
		);
		return new RootInput(prefix.getContent() + this.content, extendedSource,
												 Math.min(this.startIndex, prefix.getStartIndex()), this.endIndex);
	}

}
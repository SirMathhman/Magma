struct DivideState {};/*
	public final List<String> segments;*//*
	private final String input;*//*
	private StringBuilder buffer;*//*
	private int depth;*//*
	private int index;*//*

	public DivideState(StringBuilder buffer, int depth, List<String> segments, String input) {
		this.buffer = buffer; this.depth = depth; this.segments = segments; this.input = input;
	}*//*

	public DivideState(String input) {
		this(new StringBuilder(), 0, new ArrayList<>(), input);
	}*//*

	Stream<String> stream() {
		return segments.stream();
	}*//*

	DivideState enter() {
		this.depth = depth + 1; return this;
	}*//*

	DivideState advance() {
		segments.add(buffer.toString()); this.buffer = new StringBuilder(); return this;
	}*//*

	DivideState append(char c) {
		buffer.append(c); return this;
	}*//*

	DivideState exit() {
		this.depth = depth - 1; return this;
	}*//*

	boolean isShallow() {
		return depth == 1;
	}*//*

	boolean isLevel() {
		return depth == 0;
	}*/poppopAndAppendToTuplepopAndAppendToOption/*
*/
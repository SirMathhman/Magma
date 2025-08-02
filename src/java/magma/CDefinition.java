package magma;

record CDefinition(Option<String> maybeTypeParameter, String type, String name) implements JavaMethodHeader {
	CDefinition(final String type, final String name) {
		this(new None<>(), type, name);
	}

	@Override
	public String generate() {
		return this.maybeTypeParameter.map(value -> "<" + value + "> ").orElse("") + this.type + " " + this.name;
	}
}

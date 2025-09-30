struct FoldingDivider implements Divider {};/*
	private final Folder folder;*//*

	public FoldingDivider(Folder folder) {this.folder = folder;}*//*

	@Override
	public Stream<String> divide(String input) {
		DivideState current = new DivideState(); for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i); current = folder.fold(current, c);
		}

		return current.advance().stream();
	}*//*
*/
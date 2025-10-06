package magma.compile.rule;

import magma.option.Option;

import java.util.stream.Stream;

public class FoldingDivider implements Divider {
	private final Folder folder;

	public FoldingDivider(Folder folder) {this.folder = folder;}

	@Override
	public Stream<String> divide(String input) {
		DivideState current = new DivideState(input); while (true) {
			final Option<Character> pop = current.pop(); if (pop instanceof Option.None<Character>) break;
			if (pop instanceof Option.Some<Character>(Character c)) current = folder.fold(current, c);
		}

		return current.advance().stream();
	}

	@Override
	public String delimiter() {
		return folder.delimiter();
	}
}

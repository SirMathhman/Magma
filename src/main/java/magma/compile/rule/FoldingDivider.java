package magma.compile.rule;

import magma.list.Stream;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public class FoldingDivider implements Divider {
	private final Folder folder;

	public FoldingDivider(Folder folder) {this.folder = folder;}

	@Override
	public Stream<TokenSequence> divide(TokenSequence tokenSequence) {
		DivideState current = new DivideState(tokenSequence);
		while (true) {
			final Option<Character> pop = current.pop();
			if (pop instanceof None<Character>) break;
			if (pop instanceof Some<Character>(Character c)) current = folder.fold(current, c);
		}

		return current.advance().stream();
	}

	@Override
	public String delimiter() {
		return folder.delimiter();
	}
}

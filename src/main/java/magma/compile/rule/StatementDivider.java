package magma.compile.rule;

import java.util.ArrayList;
import java.util.stream.Stream;

public class StatementDivider implements Divider {
	@Override
	public Stream<String> divide(String afterBraces) {
		final ArrayList<String> segments = new ArrayList<>();
		StringBuilder buffer = new StringBuilder();
		int depth = 0;
		for (int i = 0; i < afterBraces.length(); i++) {
			final char c = afterBraces.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
			} else if (c == '}' && depth == 1) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
				depth--;
			} else {
				if (c == '{') depth++;
				if (c == '}') depth--;
			}
		}

		segments.add(buffer.toString());
		return segments.stream();
	}
}

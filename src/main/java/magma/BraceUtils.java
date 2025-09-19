package magma;

public class BraceUtils {
	public static String stripOuterBraces(String t) {
		if (t == null)
			return null;
		String trimmed = t.trim();
		if (!trimmed.startsWith("{"))
			return t;
		int depth = 0;
		int match = -1;
		for (int i = 0; i < trimmed.length(); i++) {
			char c = trimmed.charAt(i);
			if (c == '{')
				depth++;
			else if (c == '}') {
				depth--;
				if (depth == 0) {
					match = i;
					break;
				}
			}
		}
		if (match == trimmed.length() - 1)
			return trimmed.substring(1, trimmed.length() - 1).trim();
		return t;
	}

	public static String stripLeadingBraceBlock(String t) {
		if (t == null)
			return null;
		String trimmed = t.trim();
		if (!trimmed.startsWith("{"))
			return t;
		int depth = 0;
		int i;
		for (i = 0; i < trimmed.length(); i++) {
			char c = trimmed.charAt(i);
			if (c == '{')
				depth++;
			else if (c == '}') {
				depth--;
				if (depth == 0) {
					i++;
					break;
				}
			}
		}
		if (depth != 0)
			return t; // unmatched braces — leave as is
		// if the leading brace block covers the whole trimmed string, don't strip it
		// here
		if (i >= trimmed.length())
			return t;
		// return the remainder after the leading brace block
		return trimmed.substring(i).trim();
	}
}

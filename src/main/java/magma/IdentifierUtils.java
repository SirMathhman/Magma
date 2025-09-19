package magma;

public class IdentifierUtils {
	public static class IdParseResult {
		public final String name;
		public final int next;

		public IdParseResult(String name, int next) {
			this.name = name;
			this.next = next;
		}
	}

	public static IdParseResult parseIdentifier(String s, int pos) {
		int n = s.length();
		int i = pos;
		while (i < n && Character.isWhitespace(s.charAt(i)))
			i++;
		if (i >= n || !Character.isJavaIdentifierStart(s.charAt(i)))
			return null;
		int start = i;
		i++;
		while (i < n && Character.isJavaIdentifierPart(s.charAt(i)))
			i++;
		return new IdParseResult(s.substring(start, i), i);
	}
}

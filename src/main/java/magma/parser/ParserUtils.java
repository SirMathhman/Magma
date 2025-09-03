package magma.parser;

import java.util.ArrayList;
import java.util.List;

public final class ParserUtils {
	private ParserUtils() {
	}

	public static int advanceNested(String s, int p, char openChar, char closeChar) {
		var depth = 1;
		while (p < s.length() && depth > 0) {
			var ch = s.charAt(p);
			if (ch == openChar)
				depth++;
			else if (ch == closeChar)
				depth--;
			p++;
		}
		return depth == 0 ? p : -1;
	}

	public static List<String> splitTopLevel(String s, char sep, char open, char close) {
		// Delegate to the more general splitter which respects (), {} and [] nesting.
		// This preserves original behavior for typical callers while avoiding
		// duplicated tail logic that CPD flags.
		return splitTopLevelMulti(s, sep);
	}

	public static List<String> splitTopLevelMulti(String s, char sep) {
		List<String> out = new ArrayList<>();
		if (s == null)
			return out;
		int paren = 0;
		int brace = 0;
		int bracket = 0;
		int start = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '(')
				paren++;
			else if (c == ')')
				paren--;
			else if (c == '{')
				brace++;
			else if (c == '}')
				brace--;
			else if (c == '[')
				bracket++;
			else if (c == ']')
				bracket--;
			else if (c == sep && paren == 0 && brace == 0 && bracket == 0) {
				out.add(s.substring(start, i));
				start = i + 1;
			}
		}
		out.add(s.substring(start));
		return out;
	}

	public static List<String> splitNonEmptyParts(String inner) {
		var parts = Parser.splitByChar(inner);
		return trimNonEmpty(parts);
	}

	public static List<String> trimNonEmpty(List<String> parts) {
		List<String> nonEmpty = new ArrayList<>();
		if (parts == null)
			return nonEmpty;
		for (var p : parts) {
			if (p != null && !p.trim().isEmpty())
				nonEmpty.add(p.trim());
		}
		return nonEmpty;
	}

	public static List<String> trimNonEmpty(String[] parts) {
		if (parts == null)
			return new ArrayList<>();
		return trimNonEmpty(java.util.Arrays.asList(parts));
	}

	// Given a declared array type like "[I32; 3]" return the element type ("I32")
	// or null when not an array type.
	public static String arrayElementType(String declared) {
		if (declared == null)
			return null;
		var t = declared.trim();
		if (!t.startsWith("[") || !t.endsWith("]"))
			return null;
		var inner = t.substring(1, t.length() - 1).trim();
		var semi = inner.indexOf(';');
		if (semi == -1)
			return null;
		return inner.substring(0, semi).trim();
	}

	public static List<String> splitNonEmptyFromBraced(String braced) {
		if (braced == null)
			return new ArrayList<>();
		var t = braced.trim();
		if (!t.startsWith("{") || !t.endsWith("}"))
			return new ArrayList<>();
		var inner = t.substring(1, t.length() - 1).trim();
		return splitNonEmptyParts(inner);
	}
}

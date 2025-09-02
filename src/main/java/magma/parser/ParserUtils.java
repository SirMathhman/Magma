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
			if (ch == openChar) depth++;
			else if (ch == closeChar) depth--;
			p++;
		}
		return depth == 0 ? p : -1;
	}

	public static List<String> splitTopLevel(String s, char sep, char open, char close) {
		List<String> out = new ArrayList<>();
		if (s == null) return out;
		var depth = 0;
		var start = 0;
		for (var i = 0; i < s.length(); i++) {
			var c = s.charAt(i);
			if (c == open) depth++;
			else if (c == close) depth--;
			else if (c == sep && depth == 0) {
				out.add(s.substring(start, i));
				start = i + 1;
			}
		}
		out.add(s.substring(start));
		return out;
	}

	public static List<String> splitNonEmptyParts(String inner) {
		var parts = Parser.splitByChar(inner);
		List<String> nonEmpty = new ArrayList<>();
		for (var p : parts) {
			if (p != null && !p.trim().isEmpty()) nonEmpty.add(p.trim());
		}
		return nonEmpty;
	}

	public static List<String> splitNonEmptyFromBraced(String braced) {
		if (braced == null) return new ArrayList<>();
		var t = braced.trim();
		if (!t.startsWith("{") || !t.endsWith("}")) return new ArrayList<>();
		var inner = t.substring(1, t.length() - 1).trim();
		return splitNonEmptyParts(inner);
	}
}

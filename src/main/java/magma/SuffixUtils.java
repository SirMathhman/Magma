package magma;

public class SuffixUtils {
	public static boolean isAllowedSuffix(String s) {
		if (s == null)
			return false;
		switch (s) {
			case "U8":
			case "U16":
			case "U32":
			case "U64":
			case "I8":
			case "I16":
			case "I32":
			case "I64":
				return true;
			default:
				return false;
		}
	}
}

package magma;

public class Executor {
	public static Result<String, String> execute(String input) {
		var opt = java.util.Optional.ofNullable(input).filter(s -> !s.isEmpty());
		if (opt.isEmpty()) {
			return new Result.Ok<>("");
		}
		var s = opt.get();
		// If the input starts with one or more digits, return those leading digits as Ok
		var i = 0;
		while (i < s.length() && Character.isDigit(s.charAt(i))) {
			i++;
		}
		if (i > 0) {
			return new Result.Ok<>(s.substring(0, i));
		}
		return new Result.Err<>("Non-empty input not allowed");
	}
}

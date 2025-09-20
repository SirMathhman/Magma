package magma;

public class Executor {
	public static Result<String, String> execute(String input) {
		var opt = java.util.Optional.ofNullable(input).filter(s -> !s.isEmpty());
		if (opt.isEmpty()) {
			return new Result.Ok<>("");
		}
		return new Result.Err<>("Non-empty input not allowed");
	}
}

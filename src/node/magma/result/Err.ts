/*import java.util.function.Function;*/
/*public record Err<Value, Error>(Error error) implements Result<Value, Error> */ {
	public match(/*final Function<Value*//* Return> whenOk*//* final Function<Error*//* Return> whenErr*/) : Return {
		/*return whenErr*/.apply(this.error);
	}
	/**/}/**/

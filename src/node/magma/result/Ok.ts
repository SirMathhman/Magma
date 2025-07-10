/*import java.util.function.Function;*/
/*public record Ok<Value, Error>(Value value) implements Result<Value, Error> */ {
	public match(/*final Function<Value*//* Return> whenOk*//* final Function<Error*//* Return> whenErr*/) : Return {
		/*return whenOk*/.apply(this.value);
	}
	/**/}/**/

/*import java.util.function.Function;*/
/*public record Assignment(Definable definition, String value) */ {
	Assignment constructor(/*final Function<Definable*//* Definable> mapper*/) {
		const definition1 = this.definition;
		const definable2 = mapper.apply(/*definition1*/);
		/*return new Assignment(definable2, this.value())*/;
	}
	String constructor() {
		let " : /*+*/ = /*" + this*/.value();
	}
	/**/}/**/

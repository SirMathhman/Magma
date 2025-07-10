/*import java.util.function.Function;*/
/*public record Assignment(Definable definition, String value) */ {
	public mapDefinition(/*final Function<Definable*//* Definable> mapper*/) : Assignment {
		const definition1 = this.definition;
		const definable2 = mapper.apply(/*definition1*/);
		/*return new Assignment(definable2, this.value())*/;
	}
	public generate() : string {
		let " : /*+*/ = /*" + this*/.value();
	}
	/**/}/**/

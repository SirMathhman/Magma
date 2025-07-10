/*import java.util.Collection;*/
/*import java.util.function.Function;*/
/*import java.util.stream.Collectors;*/
/*public record Definition(Collection<String> modifiers, String name, String type) implements Header, Definable */ {
	static generateModifiers(newModifiers : /*Collection<String>*/) : string {
		/*return newModifiers.stream().map(value -> value + " ").collect(Collectors.joining())*/;
	}
	public generate() : string {
		/*return this.generateWithAfterName("")*/;
	}
	public generateWithAfterName(afterName : string) : string {
		readonly joinedModifiers : /*var*/ = Definition.generateModifiers(/*this.modifiers()*/);
		/*return joinedModifiers + this.name() + afterName + " : " + this.type()*/;
	}
	public mapModifiers(/*final Function<Collection<String>*//* Collection<String>> mapper*/) : /*Definition*/ {
		/*return new Definition(mapper.apply(this.modifiers), this.name, this.type)*/;
	}
	/**/}/**/

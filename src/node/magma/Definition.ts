/*import java.util.Collection;*/
/*import java.util.Optional;*/
/*import java.util.function.Function;*/
/*import java.util.stream.Collectors;*/
/*public record Definition(Collection<String> modifiers, String name, Optional<String> maybeType)
        implements Header, Definable */ {
	static generateModifiers(newModifiers : /*Collection<String>*/) : string {
		/*return newModifiers.stream().map(value -> value + " ").collect(Collectors.joining())*/;
	}
	public generate() : string {
		/*return this.generateWithAfterName("")*/;
	}
	public generateWithAfterName(afterName : string) : string {
		const joinedModifiers = Definition.generateModifiers(this.modifiers());
		/*return joinedModifiers + this.name() + afterName + this.maybeType.map(value -> " : " + value).orElse("")*/;
	}
	public mapModifiers(/*final Function<Collection<String>*//* Collection<String>> mapper*/) : Definition {
		/*return new Definition(mapper.apply(this.modifiers), this.name, this.maybeType)*/;
	}
	public mapType(/*final Function<String*//* Optional<String>> mapper*/) : Definable {
		/*return new Definition(this.modifiers, this.name, this.maybeType.map(mapper).orElse(this.maybeType))*/;
	}
	/**/}/**/

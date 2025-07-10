/*import java.util.Collection;*/
/*import java.util.stream.Collectors;*/
/*public record Definition(Collection<String> newModifiers, String name, String type) implements Header, Definable */ {
	static generateModifiers(readonly newModifiers : /*Collection<String>*/) : string {
		/*return newModifiers.stream().map(value -> value + " ").collect(Collectors.joining());*/
	}
	generate() : string {
		/*return this.generateWithAfterName("");*/
	}
	generateWithAfterName(readonly afterName : string) : string {
		/*final var joinedModifiers = Definition.generateModifiers(this.newModifiers());*/
		/*return joinedModifiers + this.name() + afterName + " : " + this.type();*/
	}
	/**/}/**/

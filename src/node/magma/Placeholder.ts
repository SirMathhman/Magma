
class Placeholder {
	/*private final String value;*/
	constructor (final value : string) {
		this.value = value;
	}
	static generate(final input : string) : string {
		final let replaced : any = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}
	public generateWithAfterName(final afterName : string) : string {
		return Placeholder.generate(this.value) + afterName;
	}
	public generate() : string {
		return this.value;
	}
}


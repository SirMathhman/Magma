
class Placeholder {
	/*private final String value;*/
	constructor (value : string) {
		this.value = value;/*
    */}
	generate(input : string) : string {
		replaced : any = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";/*
    */}
	generateWithAfterName(afterName : string) : string {
		return Placeholder.generate(this.value) + afterName;/*
    */}
	generate() : string {
		return this.value;/*
    */}
	}


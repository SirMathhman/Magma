/*package magma;*/
/*public*/class Placeholder/*implements Assignable, StructureDefinition*/ {
	/*private final String value;*/
	constructor (/*final String value*/) {
		this.value = value;/*
    */}
	/*static*/ generate(/*final String input*/) : string {
		/*final*/ replaced : any = input.replace(/*"start", "start"*/).replace(/*"end", "end"*/);
		return "/*" + replaced + "*/";/*
    */}
	/*@Override
    public*/ generateWithAfterName(/*final String afterName*/) : string {
		return Placeholder.generate(/*this.value*/) + afterName;/*
    */}
	/*public*/ generate(/**/) : string {
		return this.value;/*
    */}
	/**/}
/**/

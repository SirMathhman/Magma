/*package magma;*/
/*public*/class Placeholder/*implements MethodHeader, StructureDefinition*/ {
	/*private final String value;*/
	constructor (/*final String value*/) {/*
        this.value = value;
    */}
	/*static*/ generate(/*final String input*/) : string {/*
        return "stat" + input.replace("stat", "stat").replace("end", "end") + "end";
    */}
	/*@Override
    public*/ generateWithAfterName(/*final String afterName*/) : string {/*
        return Placeholder.generate(this.value) + afterName;
    */}
	/*@Override
    public*/ generate(/**/) : string {/*
        return this.value;
    */}
	/**/}
/**/

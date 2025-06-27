
class Placeholder/*implements Assignable, StructureDefinition*/ {
	/*private final String value;*/
	constructor (value : string) {/*{
        this.value = value;
    }*//**/}
	generate(input : string) : string {/*{
        final var replaced = input.replace("start", "start").replace("end", "end");
        return "start" + replaced + "end";
    }*//**/}
	generateWithAfterName(afterName : string) : string {/*{
        return Placeholder.generate(this.value) + afterName;
    }*//**/}
	generate() : string {/*{
        return this.value;
    }*//**/}
	/**/}


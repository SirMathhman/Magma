
class Definition {
	generateWithAfterName(afterName : string) : string {
		return this.name + afterName + " : " + this.type;}
	generate() : string {
		return this.generateWithAfterName("");}
	}


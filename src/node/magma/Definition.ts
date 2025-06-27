

class Definition {
	generateWithAfterName(afterName : string) : string {
		let joinedModifiers : any = this.getString();
		return joinedModifiers + this.name + afterName + " : " + this.type;
	}
	getString() : string {
		if (this.modifiers.isEmpty())
			return "";
		return this.modifiers.stream().map(value => value + " ").collect(Collectors.joining());
	}
	generate() : string {
		return this.generateWithAfterName("");
	}
	withModifier(modifier : string) : Definition {
		return new Definition(this.modifiers.add(modifier), this.beforeType, this.name, this.type);
	}
}


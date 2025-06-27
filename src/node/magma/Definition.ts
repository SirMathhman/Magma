

class Definition {
	generateWithAfterName(afterName : string) : string {/*
        final String joinedModifiers;*/
		if (this.modifiers.isEmpty())
			joinedModifiers = "";
		else 
			joinedModifiers = this.modifiers.stream().map(value => value + " ").collect(Collectors.joining());
		return joinedModifiers + this.name + afterName + " : " + this.type;
	}
	generate() : string {
		return this.generateWithAfterName("");
	}
	withModifier(modifier : string) : Definition {
		return new Definition(this.modifiers.add(modifier), this.beforeType, this.name, this.type);
	}
}


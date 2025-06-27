


class Definition {
	public generateWithAfterName(final afterName : string) : string {/*
        final String joinedTypeParams;*/
		if (this.typeParams.isEmpty())
			joinedTypeParams = "";
		else 
			joinedTypeParams = " < " + String.join(", ", this.typeParams) + " > ";
		final let joinedModifiers : any = this.getString();
		return joinedModifiers + this.name + joinedTypeParams + afterName + " : " + this.type;
	}
	private getString() : string {
		if (this.modifiers.isEmpty())
			return "";
		return this.modifiers.stream().map(value => value + " ").collect(Collectors.joining());
	}
	public generate() : string {
		return this.generateWithAfterName("");
	}
	public withModifier(final modifier : string) : Definition {
		return new Definition(this.annotations, this.modifiers.add(modifier), this.name, this.type, this.typeParams);
	}
}


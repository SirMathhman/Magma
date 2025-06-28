



class Definition {
	generateWithAfterName(final afterName : string) : string {/*
        final String joinedTypeParams;*/
		if (this.typeParams.isEmpty())
			joinedTypeParams = "";
		else 
			joinedTypeParams = " < " + String.join(", ", this.typeParams) + " > ";
		final let joinedModifiers : any = this.getString();
		return joinedModifiers + this.name + joinedTypeParams + afterName + " : " + this.type;
	}
	getString() : string {
		if (this.modifiers.isEmpty())
			return "";
		return this.modifiers.stream().map(value => value + " ").collect(Collectors.joining());
	}
	generate() : string {
		return this.generateWithAfterName("");
	}
	withModifier(final modifier : string) : Definition {
		return new Definition(this.annotations, this.modifiers.add(modifier), this.typeParams, this.name, this.type);
	}
	mapModifiers(final mapper : Function<ListLike<string>, ListLike<string>>) : Definition {
		return new Definition(this.annotations, mapper.apply(this.modifiers), this.typeParams, this.name, this.type);
	}
}


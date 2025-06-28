



class Definition {
	generateWithAfterName(afterName : string) : string {/*
        final String joinedTypeParams;*/
		if (this.typeParams.isEmpty())
			joinedTypeParams = "";
		else 
			joinedTypeParams = " < " + String.join(", ", this.typeParams) + " > ";
		joinedModifiers : any = this.getString();
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
	withModifier(modifier : string) : Definition {
		return new Definition(this.annotations, this.modifiers.add(modifier), this.typeParams, this.name, this.type);
	}
	mapModifiers(mapper : Function<ListLike<string>, ListLike<string>>) : Definition {
		return new Definition(this.annotations, mapper.apply(this.modifiers), this.typeParams, this.name, this.type);
	}
}


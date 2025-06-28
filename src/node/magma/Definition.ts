



class Definition {
	annotations : ListLike<string>;
	modifiers : ListLike<string>;
	typeParams : List<string>;
	name : string;
	type : string;
	generateWithAfterName(afterName : string) : string {
		let joinedTypeParams : any = this.getJoinedTypeParams();
		let joinedModifiers : any = this.getString();
		return joinedModifiers + this.name + joinedTypeParams + afterName + " : " + this.type;
	}
	getJoinedTypeParams() : string {
		if (this.typeParams.isEmpty())
			return "";
		else 
			return " < " + String.join(", ", this.typeParams) + " > ";
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



class Definition {Some[value=
	modifiers : ListLike<Some[value=]>;
	typeParams : ListLike<Some[value=]>;
	name : string;
	type : string;]
	constructor (Some[value=, modifiers : ListLike<Some[value=]>, typeParams : ListLike<Some[value=]>, name : string, type : string]) {Some[value=
		this.modifiers = modifiers;
		this.typeParams = typeParams;
		this.name = name;
		this.type = type;]
	}
	getJoinedTypeParams() : string {
		else 
			return " < " + this.typeParams.stream().collect(new Joiner(", ")) + " > ";
	}
	getString() : string {
		return /*this.modifiers.stream().map(value -> value + " ").collect(new Joiner()).orElse("")*/;
	}
	generate() : string {
	}
	withModifier() : Definition {
	}
	mapModifiers() : Definition {
	}
}



class StructureHeader {
	type : string;
	annotations : ListLike<string>;
	beforeKeyword : string;
	name : string;
	maybeAfterImplements : Optional<string>;
	parameters : ListLike<Parameter>;
	constructor (type : string, annotations : ListLike<string>, beforeKeyword : string, name : string, maybeAfterImplements : Optional<string>, parameters : ListLike<Parameter>) {
		this.type = type;
		this.annotations = annotations;
		this.beforeKeyword = beforeKeyword;
		this.name = name;
		this.maybeAfterImplements = maybeAfterImplements;
		this.parameters = parameters;
	}
	generate() : string {
		return this.type + " " + this.name;
	}
}



class StructureHeader {
	type : string;
	annotations : ListLike<string>;
	beforeKeyword : string;
	name : string;
	maybeAfterImplements : Optional<string>;
	parameters : ListLike<Parameter>;
	constructor (type : string, annotations : ListLike<string>, beforeKeyword : string, name : string, maybeAfterImplements : Optional<string>, parameters : ListLike<Parameter>) {
	}
	generate() : string {
		return this.type + " " + this.name;
	}
}


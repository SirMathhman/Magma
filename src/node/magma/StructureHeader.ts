class StructureHeader {
	annotations : ListLike<Some[value=]>;
	beforeKeyword : string;
	name : string;
	maybeAfterImplements : Optional<Some[value=]>;
	parameters : ListLike<Some[value=]>;
	constructor (, annotations : ListLike<Some[value=]>, beforeKeyword : string, name : string, maybeAfterImplements : Optional<Some[value=]>, parameters : ListLike<Some[value=]>) {
		this.annotations = annotations;
		this.beforeKeyword = beforeKeyword;
		this.name = name;
		this.maybeAfterImplements = maybeAfterImplements;
		this.parameters = parameters;
	}
}


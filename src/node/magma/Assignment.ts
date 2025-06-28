

class Assignment {
	assignable : Assignable;
	value : string;
	constructor (assignable : Assignable, value : string) {
		this.assignable = assignable;
		this.value = value;
	}
	generate() : string {
		return this.assignable().generate() + " = " + this.value();
	}
	mapAssignable(mapper : Function<Assignable, Assignable>) : Assignment {
		return new Assignment(mapper.apply(this.assignable), this.value);
	}
}


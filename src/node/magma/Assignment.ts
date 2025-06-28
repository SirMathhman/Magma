

class Assignment {
	generate() : string {
		return this.assignable().generate() + " = " + this.value();
	}
	mapAssignable(mapper : Function<Assignable, Assignable>) : Assignment {
		return new Assignment(mapper.apply(this.assignable), this.value);
	}
}


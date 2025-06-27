/*package magma;*/
/*public*/class Definition/*implements Assignable*/ {
	/*@Override
    public*/ generateWithAfterName(/*final String afterName*/) : string {
		return Placeholder.generate(this.beforeType) + " " + this.name + afterName + " : " + this.type;/*
    */}
	/*@Override
    public*/ generate(/**/) : string {
		return this.generateWithAfterName("");/*
    */}
	/**/}
/**/

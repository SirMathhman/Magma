import { Caller } from "../../../../magma/app/compile/value/Caller";
export class ConstructionCaller implements Caller {
	type: string;
	constructor (type: string) {
		this.type = type;
	}
	generate(): string {
		return "new " + this.type/*unknown*/;
	}
}

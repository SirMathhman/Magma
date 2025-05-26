import { Caller } from "../../../../magma/app/compile/value/Caller";
export class ConstructionCaller implements Caller {
	right: string;
	constructor (right: string) {
		this.right = right;
	}
	generate(): string {
		return "new " + this.right/*unknown*/;
	}
}

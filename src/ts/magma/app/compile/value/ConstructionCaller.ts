import { Node } from "../../../../magma/app/compile/node/Node";
export class ConstructionCaller implements Node {
	type: string;
	constructor (type: string) {
		this.type = type;
	}
	generate(): string {
		return "new " + this.type/*unknown*/;
	}
	is(type: string): boolean {
		return "construction".equals(type)/*unknown*/;
	}
}

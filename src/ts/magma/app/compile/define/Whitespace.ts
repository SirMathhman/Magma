import { Node } from "../../../../magma/app/compile/node/Node";
export class Whitespace implements Node {
	is(type: string): boolean {
		return "whitespace".equals(type)/*unknown*/;
	}
}

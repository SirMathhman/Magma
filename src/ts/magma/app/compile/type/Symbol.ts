import { Node } from "../../../../magma/app/compile/node/Node";
export class Symbol implements Node {
	is(type: string): boolean {
		return false/*unknown*/;
	}
}

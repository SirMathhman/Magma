import { Node } from "../../../../magma/app/compile/node/Node";
export class TemplateNode implements Node {
	base: string;
	args: magma.api.collect.list.List<Node>;
	constructor (base: string, args: magma.api.collect.list.List<Node>) {
		this.base = base;
		this.args = args;
	}
	is(type: string): boolean {
		return "template".equals(type)/*unknown*/;
	}
}

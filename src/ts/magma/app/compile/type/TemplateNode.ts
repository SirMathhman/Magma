import { Node } from "../../../../magma/app/compile/node/Node";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
import { Merger } from "../../../../magma/app/compile/merge/Merger";
import { ValueMerger } from "../../../../magma/app/compile/merge/ValueMerger";
export class TemplateNode implements Node {
	base: string;
	args: Iterable<string>;
	constructor (base: string, args: Iterable<string>) {
		this.base = base;
		this.args = args;
	}
	generateNode(): string {
		return this.base + "<" + Merger.generateAll(this.args, new ValueMerger()) + ">"/*unknown*/;
	}
	isFunctional(): boolean {
		return false/*unknown*/;
	}
	isVar(): boolean {
		return false/*unknown*/;
	}
	generateBeforeName(): string {
		return ""/*unknown*/;
	}
	generateSimple(): string {
		return this.base/*unknown*/;
	}
	is(type: string): boolean {
		return "template".equals(type)/*unknown*/;
	}
}

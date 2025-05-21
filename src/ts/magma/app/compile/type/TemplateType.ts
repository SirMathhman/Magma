import { Type } from "../../../../magma/app/compile/type/Type";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
import { Merger } from "../../../../magma/app/compile/merge/Merger";
import { ValueMerger } from "../../../../magma/app/compile/merge/ValueMerger";
export class TemplateType implements Type {
	base: string;
	args: Iterable<string>;
	constructor (base: string, args: Iterable<string>) {
		this.base = base;
		this.args = args;
	}
	generate(): string {
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
}

import { Type } from "../../../../magma/app/compile/type/Type";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
export class TemplateType implements Type {
	base: string;
	args: Iterable<string>;
	constructor (base: string, args: Iterable<string>) {
		this.base = base;
		this.args = args;
	}
	isFunctional(): boolean {
		return false/*unknown*/;
	}
	isVar(): boolean {
		return false/*unknown*/;
	}
	generateBeforeName(): string {
		return ""/*string*/;
	}
	generateSimple(): string {
		return this.base/*unknown*/;
	}
}

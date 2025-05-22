import { Definition } from "../../../../magma/app/compile/define/Definition";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
import { TypeCompiler } from "../../../../magma/app/TypeCompiler";
export class Placeholder {
	value: string;
	constructor (value: string) {
		this.value = value;
	}
	static fromValue(value: string): string {
		let replaced = value.replace("/*", "start").replace("*/", "end")/*unknown*/;
		return "/*" + replaced + "*/"/*unknown*/;
	}
	static fromNode(placeholder: Placeholder): string {
		return Placeholder.fromValue(placeholder.value)/*unknown*/;
	}
	generate(): string {
		return Placeholder.fromValue(this.value)/*unknown*/;
	}
	isFunctional(): boolean {
		return false/*unknown*/;
	}
	asDefinition(): Option<Definition> {
		return new None<Definition>()/*unknown*/;
	}
	isVar(): boolean {
		return false/*unknown*/;
	}
	generateBeforeName(): string {
		return ""/*string*/;
	}
	generateSimple(): string {
		return TypeCompiler.generateType(this)/*unknown*/;
	}
}

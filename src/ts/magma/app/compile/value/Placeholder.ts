import { Definition } from "../../../../magma/app/compile/define/Definition";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
import { Type } from "../../../../magma/app/compile/type/Type";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { PrimitiveType } from "../../../../magma/app/compile/type/PrimitiveType";
import { ValueCompiler } from "../../../../magma/app/ValueCompiler";
export class Placeholder {
	input: string;
	constructor (input: string) {
		this.input = input;
	}
	static generatePlaceholder(input: string): string {
		let replaced = input.replace("/*", "start").replace("*/", "end")/*unknown*/;
		return "/*" + replaced + "*/"/*unknown*/;
	}
	generate(): string {
		return generatePlaceholder(this.input)/*unknown*/;
	}
	isFunctional(): boolean {
		return false/*unknown*/;
	}
	asDefinition(): Option<Definition> {
		return new None<Definition>()/*unknown*/;
	}
	resolve(state: CompileState): Type {
		return PrimitiveType.Unknown/*unknown*/;
	}
	isVar(): boolean {
		return false/*unknown*/;
	}
	generateBeforeName(): string {
		return ""/*unknown*/;
	}
	generateSimple(): string {
		return ValueCompiler.generateValue(this)/*unknown*/;
	}
}

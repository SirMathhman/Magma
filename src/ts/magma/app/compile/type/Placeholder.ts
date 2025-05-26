import { Definition } from "../../../../magma/app/compile/define/Definition";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
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
	generateNode(): string {
		return Placeholder.generatePlaceholder(this.input)/*unknown*/;
	}
	generate(): string {
		return Placeholder.generatePlaceholder(this.input)/*unknown*/;
	}
	asDefinition(): Option<Definition> {
		return new None<Definition>()/*unknown*/;
	}
	generateBeforeName(): string {
		return ""/*unknown*/;
	}
	generateSimple(): string {
		return ValueCompiler.generateValue(this)/*unknown*/;
	}
	is(type: string): boolean {
		return false/*unknown*/;
	}
}

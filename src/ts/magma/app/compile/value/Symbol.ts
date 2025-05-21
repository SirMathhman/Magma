import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
import { TypeCompiler } from "../../../../magma/app/TypeCompiler";
export class Symbol {
	value: string;
	constructor (value: string) {
		this.value = value;
	}
	generate(): string {
		return this.value/*unknown*/;
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
	generateAsEnumValue(structureName: string): Option<string> {
		return new None<string>()/*unknown*/;
	}
	generateSimple(): string {
		return TypeCompiler.generateType(this)/*unknown*/;
	}
}

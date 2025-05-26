import { Type } from "../../../../magma/app/compile/type/Type";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { Definition } from "../../../../magma/app/compile/define/Definition";
import { PrimitiveType } from "../../../../magma/app/compile/type/PrimitiveType";
import { Value } from "../../../../magma/app/compile/value/Value";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { None } from "../../../../magma/api/option/None";
export class Symbol {
	value: string;
	constructor (value: string) {
		this.value = value;
	}
	generate(): string {
		return this.value/*unknown*/;
	}
	resolve(state: CompileState): Type {
		return state.stack().resolveValue(this.value).map((definition: Definition) => {
			return definition.findType()/*unknown*/;
		}).orElse(PrimitiveType.Unknown)/*unknown*/;
	}
	toValue(): Option<Value> {
		return new Some<Value>(this)/*unknown*/;
	}
	findChild(): Option<Value> {
		return new None<Value>()/*unknown*/;
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
	generateAsEnumValue(structureName: string): Option<string> {
		return new None<string>()/*unknown*/;
	}
	generateSimple(): string {
		return this.generate()/*unknown*/;
	}
}

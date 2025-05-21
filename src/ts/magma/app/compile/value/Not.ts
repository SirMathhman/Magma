import { Value } from "../../../../magma/app/compile/value/Value";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { None } from "../../../../magma/api/option/None";
import { Type } from "../../../../magma/app/compile/type/Type";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { PrimitiveType } from "../../../../magma/app/compile/type/PrimitiveType";
export class Not implements Value {
	child: string;
	constructor (child: string) {
		this.child = child;
	}
	generate(): string {
		return this.child/*unknown*/;
	}
	toValue(): Option<Value> {
		return new Some<Value>(this)/*unknown*/;
	}
	findChild(): Option<Value> {
		return new None<Value>()/*unknown*/;
	}
	resolve(state: CompileState): Type {
		return PrimitiveType.Unknown/*unknown*/;
	}
	generateAsEnumValue(structureName: string): Option<string> {
		return new None<string>()/*unknown*/;
	}
}

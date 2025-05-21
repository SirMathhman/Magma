import { Value } from "../../../../magma/app/compile/value/Value";
import { ValueCompiler } from "../../../../magma/app/ValueCompiler";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { Type } from "../../../../magma/app/compile/type/Type";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { PrimitiveType } from "../../../../magma/app/compile/type/PrimitiveType";
import { None } from "../../../../magma/api/option/None";
export class AccessValue implements Value {
	child: Value;
	property: string;
	constructor (child: Value, property: string) {
		this.child = child;
		this.property = property;
	}
	generate(): string {
		return ValueCompiler.generateCaller(this.child) + "." + this.property/*unknown*/;
	}
	toValue(): Option<Value> {
		return new Some<Value>(this)/*unknown*/;
	}
	findChild(): Option<Value> {
		return new Some<Value>(this.child)/*unknown*/;
	}
	resolve(state: CompileState): Type {
		return PrimitiveType.Unknown/*unknown*/;
	}
	generateAsEnumValue(structureName: string): Option<string> {
		return new None<string>()/*unknown*/;
	}
}

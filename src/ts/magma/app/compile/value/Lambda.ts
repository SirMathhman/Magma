import { Value } from "../../../../magma/app/compile/value/Value";
import { Definition } from "../../../../magma/app/compile/define/Definition";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
import { Joiner } from "../../../../magma/api/collect/Joiner";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { None } from "../../../../magma/api/option/None";
import { Type } from "../../../../magma/app/compile/type/Type";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { PrimitiveType } from "../../../../magma/app/compile/type/PrimitiveType";
export class Lambda implements Value {
	paramNames: Iterable<Definition>;
	content: string;
	constructor (paramNames: Iterable<Definition>, content: string) {
		this.paramNames = paramNames;
		this.content = content;
	}
	generate(): string {
		let joinedParamNames = this.paramNames.iter().map((definition: Definition) => {
			return definition.generate()/*unknown*/;
		}).collect(new Joiner(", ")).orElse("")/*unknown*/;
		return "(" + joinedParamNames + ")" + " => " + this.content/*unknown*/;
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

import { Value } from "../../../../magma/app/compile/value/Value";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
export class AccessValue implements Value {
	child: Value;
	property: string;
	constructor (child: Value, property: string) {
		this.child = child;
		this.property = property;
	}
	generate(): string {
		return this.child.generate() + "." + this.property/*unknown*/;
	}
	findChild(): Option<Value> {
		return new Some<Value>(this.child)/*unknown*/;
	}
	is(type: string): boolean {
		return "access".equals(type)/*unknown*/;
	}
}

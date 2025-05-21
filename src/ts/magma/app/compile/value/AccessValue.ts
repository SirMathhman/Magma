import { Value } from "../../../../magma/app/compile/value/Value";
export class AccessValue implements Value {
	child: Value;
	property: string;
	constructor (child: Value, property: string) {
		this.child = child;
		this.property = property;
	}
}

import { Value } from "../../../../magma/app/compile/value/Value";
export class StringValue implements Value {
	value: string;
	constructor (value: string) {
		this.value = value;
	}
}

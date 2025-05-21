import { Value } from "../../../../magma/app/compile/value/Value";
export class Not implements Value {
	child: string;
	constructor (child: string) {
		this.child = child;
	}
}

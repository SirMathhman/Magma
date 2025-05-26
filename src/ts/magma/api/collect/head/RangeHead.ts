import { Head } from "../../../../magma/api/collect/head/Head";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
import { Some } from "../../../../magma/api/option/Some";
export class RangeHead implements Head<number> {
	length: number;
	counter: number;
	constructor (length: number) {
		this.length/*unknown*/ = length/*number*/;
		this.counter/*unknown*/ = 0/*unknown*/;
	}
	next(): Option<number> {
		if (this.counter >= this.length/*unknown*/){
			return new None<number>()/*unknown*/;
		}
		let value = this.counter/*unknown*/;
		this.counter/*unknown*/++;
		return new Some<number>(value)/*unknown*/;
	}
}

import { Head } from "../../../../magma/api/collect/head/Head";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
import { Some } from "../../../../magma/api/option/Some";
export class SingleHead<T> implements Head<T> {
	element: T;
	retrieved: boolean;
	constructor (element: T) {
		this.element/*unknown*/ = element/*T*/;
		this.retrieved/*unknown*/ = false/*unknown*/;
	}
	next(): Option<T> {
		if (this.retrieved/*unknown*/){
			return new None<T>()/*unknown*/;
		}
		this.retrieved/*unknown*/ = true/*unknown*/;
		return new Some<T>(this.element)/*unknown*/;
	}
}

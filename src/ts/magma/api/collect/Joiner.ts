import { Option } from "../../../magma/api/option/Option";
import { Collector } from "../../../magma/api/collect/Collector";
import { None } from "../../../magma/api/option/None";
import { Some } from "../../../magma/api/option/Some";
export class Joiner implements Collector<string, Option<string>> {
	delimiter: string;
	constructor (delimiter: string) {
		this.delimiter = delimiter;
	}
	static empty(): Joiner {
		return new Joiner("")/*unknown*/;
	}
	createInitial(): Option<string> {
		return new None<string>()/*unknown*/;
	}
	fold(maybe: Option<string>, element: string): Option<string> {
		return new Some<string>(maybe.map((inner: string) => inner + this.delimiter + element/*unknown*/).orElse(element))/*unknown*/;
	}
}

import { Selector } from "../../../../magma/app/compile/select/Selector";
import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Option } from "../../../../magma/api/option/Option";
import { List } from "../../../../magma/api/collect/list/List";
import { Joiner } from "../../../../magma/api/collect/Joiner";
import { Some } from "../../../../magma/api/option/Some";
import { Tuple2Impl } from "../../../../magma/api/Tuple2Impl";
export class LastSelector implements Selector {
	delimiter: string;
	constructor (delimiter: string) {
		this.delimiter = delimiter;
	}
	select(divisions: List<string>): Option<Tuple2<string, string>> {
		let beforeLast = divisions.subList(0, divisions.size() - 1).orElse(divisions)/*unknown*/;
		let last = divisions.findLast().orElse("")/*unknown*/;
		let joined = beforeLast.iter().collect(new Joiner(this.delimiter)).orElse("")/*unknown*/;
		return new Some<Tuple2<string, string>>(new Tuple2Impl<string, string>(joined, last))/*unknown*/;
	}
}

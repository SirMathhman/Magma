import { Selector } from "../../../../magma/app/compile/select/Selector";
import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Option } from "../../../../magma/api/option/Option";
import { List } from "../../../../magma/api/collect/list/List";
import { Joiner } from "../../../../magma/api/collect/Joiner";
import { Some } from "../../../../magma/api/option/Some";
import { Tuple2Impl } from "../../../../magma/api/Tuple2Impl";
export class FirstSelector implements Selector {
	delimiter: string;
	constructor (delimiter: string) {
		this.delimiter = delimiter;
	}
	select(divisions: List<string>): Option<Tuple2<string, string>> {
		let first = divisions.findFirst().orElse("")/*unknown*/;
		let afterFirst = divisions.subList(1, divisions.size()).orElse(divisions).iter().collect(new Joiner(this.delimiter())).orElse("")/*unknown*/;
		return new Some<Tuple2<string, string>>(new Tuple2Impl<string, string>(first, afterFirst))/*unknown*/;
	}
}

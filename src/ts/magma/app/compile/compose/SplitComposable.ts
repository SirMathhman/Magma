import { Composable } from "../../../../magma/app/compile/compose/Composable";
import { Splitter } from "../../../../magma/app/compile/split/Splitter";
import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Option } from "../../../../magma/api/option/Option";
import { LocatingSplitter } from "../../../../magma/app/compile/split/LocatingSplitter";
import { LastLocator } from "../../../../magma/app/compile/locate/LastLocator";
export class SplitComposable<T> implements Composable<string, T> {
	splitter: Splitter;
	mapper: Composable<Tuple2<string, string>, T>;
	constructor (splitter: Splitter, mapper: Composable<Tuple2<string, string>, T>) {
		this.splitter = splitter;
		this.mapper = mapper;
	}
	static compileLast<T>(input: string, infix: string, mapper: (arg0 : string, arg1 : string) => Option<T>): Option<T> {
		let splitter1: Splitter = new LocatingSplitter(infix, new LastLocator())/*unknown*/;
		return new SplitComposable<T>(splitter1, Composable.toComposable(mapper)).apply(input)/*unknown*/;
	}
	apply(input: string): Option<T> {
		return this.splitter().apply(input).flatMap(this.mapper.apply)/*unknown*/;
	}
}

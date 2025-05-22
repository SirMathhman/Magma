import { Composable } from "../../../../magma/app/compile/compose/Composable";
import { Splitter } from "../../../../magma/app/compile/split/Splitter";
import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Option } from "../../../../magma/api/option/Option";
import { LocatingSplitter } from "../../../../magma/app/compile/split/LocatingSplitter";
import { LastLocator } from "../../../../magma/app/compile/locate/LastLocator";
import { FirstLocator } from "../../../../magma/app/compile/locate/FirstLocator";
export class Split<T> implements Composable<string, T> {
	splitter: Splitter;
	mapper: Composable<Tuple2<string, string>, T>;
	constructor (splitter: Splitter, mapper: Composable<Tuple2<string, string>, T>) {
		this.splitter = splitter;
		this.mapper = mapper;
	}
	static last<T>(infix: string, mapper: (arg0 : string, arg1 : string) => Option<T>): Composable<string, T> {
		return new Split<T>(new LocatingSplitter(infix, new LastLocator()), Composable.toComposable(mapper))/*unknown*/;
	}
	static first<T>(infix: string, mapper: (arg0 : string, arg1 : string) => Option<T>): Composable<string, T> {
		return new Split<T>(new LocatingSplitter(infix, new FirstLocator()), Composable.toComposable(mapper))/*unknown*/;
	}
	apply(input: string): Option<T> {
		return this.splitter().apply(input).flatMap(this.mapper.apply)/*unknown*/;
	}
}

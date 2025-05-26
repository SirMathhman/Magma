import { Option } from "../../../magma/api/option/Option";
import { Tuple2 } from "../../../magma/api/Tuple2";
import { Tuple2Impl } from "../../../magma/api/Tuple2Impl";
export class None<T> implements Option<T> {
	map<R>(mapper: (arg0 : T) => R): Option<R> {
		return new None<R>()/*unknown*/;
	}
	orElse(other: T): T {
		return other/*T*/;
	}
	orElseGet(supplier: () => T): T {
		return supplier()/*unknown*/;
	}
	isPresent(): boolean {
		return false/*unknown*/;
	}
	ifPresent(consumer: (arg0 : T) => void): void {
	}
	or(other: () => Option<T>): Option<T> {
		return other()/*unknown*/;
	}
	flatMap<R>(mapper: (arg0 : T) => Option<R>): Option<R> {
		return new None<R>()/*unknown*/;
	}
	filter(predicate: (arg0 : T) => boolean): Option<T> {
		return new None<T>()/*unknown*/;
	}
	toTuple(other: T): Tuple2<boolean, T> {
		return new Tuple2Impl<boolean, T>(false, other)/*unknown*/;
	}
	and<R>(other: () => Option<R>): Option<Tuple2<T, R>> {
		return new None<Tuple2<T, R>>()/*unknown*/;
	}
}

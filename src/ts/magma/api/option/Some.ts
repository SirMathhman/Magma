import { Option } from "../../../magma/api/option/Option";
import { None } from "../../../magma/api/option/None";
import { Tuple2 } from "../../../magma/api/Tuple2";
import { Tuple2Impl } from "../../../magma/api/Tuple2Impl";
export class Some<T> implements Option<T> {
	value: T;
	constructor (value: T) {
		this.value = value;
	}
	map<R>(mapper: (arg0 : T) => R): Option<R> {
		return new Some<R>(mapper(this.value))/*unknown*/;
	}
	orElse(other: T): T {
		return this.value/*unknown*/;
	}
	orElseGet(supplier: () => T): T {
		return this.value/*unknown*/;
	}
	isPresent(): boolean {
		return true/*unknown*/;
	}
	ifPresent(consumer: (arg0 : T) => void): void {
		consumer(this.value)/*unknown*/;
	}
	or(other: () => Option<T>): Option<T> {
		return this/*unknown*/;
	}
	flatMap<R>(mapper: (arg0 : T) => Option<R>): Option<R> {
		return mapper(this.value)/*unknown*/;
	}
	filter(predicate: (arg0 : T) => boolean): Option<T> {
		if (predicate(this.value)/*unknown*/){
			return this/*unknown*/;
		}
		return new None<T>()/*unknown*/;
	}
	toTuple(other: T): Tuple2<boolean, T> {
		return new Tuple2Impl<boolean, T>(true, this.value)/*unknown*/;
	}
	and<R>(other: () => Option<R>): Option<Tuple2<T, R>> {
		return other().map((otherValue: R) => new Tuple2Impl<T, R>(this.value, otherValue)/*unknown*/)/*unknown*/;
	}
}

// Auto-generated from magma/option/Some.java
import { Tuple } from "../Tuple";
import { Option } from "./Option";
export class Some<T> implements Option<T> {
	isPresent(): boolean {
		return true;
	}
	get(): T {
		return value;
	}
	map<U>(mapper: (arg0: T) => U): Option<U> {
		return new Some<>(mapper.apply(value));
	}
	flatMap<U>(mapper: (arg0: T) => Option<U>): Option<U> {
		return mapper.apply(value);
	}
	orElse(other: Option<T>): Option<T> {
		return this;
	}
	toTuple(defaultValue: T): Tuple<Boolean, T> {
		return new Tuple<>(true, value);
	}
}

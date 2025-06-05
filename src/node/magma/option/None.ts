// Auto-generated from magma/option/None.java
import { Tuple } from "../Tuple";
import { Option } from "./Option";
export class None<T> implements Option<T> {
	isPresent(): boolean {
		return false;
	}
	get(): T {
		throw new java.util.NoSuchElementException("No value present");
	}
	map<U>(mapper: (arg0: T) => U): Option<U> {
		return new None<>();
	}
	flatMap<U>(mapper: (arg0: T) => Option<U>): Option<U> {
		return new None<>();
	}
	orElse(other: Option<T>): Option<T> {
		return other;
	}
	toTuple(defaultValue: T): Tuple<boolean, T> {
		return new Tuple<>(false, defaultValue);
	}
}

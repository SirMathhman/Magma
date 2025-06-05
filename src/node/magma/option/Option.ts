// Auto-generated from magma/option/Option.java
import { Tuple } from "../Tuple";
export interface Option<T> {
	isPresent(): boolean;
	get(): T;
	map<U>(mapper: (arg0: T) => U): Option<U>;
	flatMap<U>(mapper: (arg0: T) => Option<U>): Option<U>;
	orElse(other: Option<T>): Option<T>;
	toTuple(defaultValue: T): Tuple<Boolean, T>;
	ifPresent(action: (arg0: T) => void): void;
}

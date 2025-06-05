// Auto-generated from magma/result/Ok.java
import { Result } from "./Result";
export class Ok<T, X> implements Result<T, X> {
	value(): T {
		return value;
	}
	isOk(): boolean {
		return true;
	}
	isErr(): boolean {
		return false;
	}
	mapValue<U>(mapper: (arg0: T) => U): Result<U, X> {
		return new Ok<>(mapper.apply(value));
	}
	flatMapValue<U>(mapper: (arg0: T) => Result<U, X>): Result<U, X> {
		return mapper.apply(value);
	}
	match<R>(whenOk: (arg0: T) => R, whenErr: (arg0: X) => R): R {
		return whenOk.apply(value);
	}
}

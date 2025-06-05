// Auto-generated from magma/result/Err.java
import { Result } from "./Result";
export class Err<T, X> implements Result<T, X> {
	error(): X {
		return error;
	}
	isOk(): boolean {
		return false;
	}
	isErr(): boolean {
		return true;
	}
	mapValue<U>(mapper: (arg0: T) => U): Result<U, X> {
		return new Err<>(error);
	}
	flatMapValue<U>(mapper: (arg0: T) => Result<U, X>): Result<U, X> {
		return new Err<>(error);
	}
	match<R>(whenOk: (arg0: T) => R, whenErr: (arg0: X) => R): R {
		return whenErr.apply(error);
	}
}

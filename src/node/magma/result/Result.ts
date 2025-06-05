// Auto-generated from magma/result/Result.java
export interface Result<T, X> {
	isOk(): boolean;
	isErr(): boolean;
	mapValue<U>(mapper: (arg0: T) => U): Result<U, X>;
	flatMapValue<U>(mapper: (arg0: T) => Result<U, X>): Result<U, X>;
	match<R>(whenOk: (arg0: T) => R, whenErr: (arg0: X) => R): R;
}

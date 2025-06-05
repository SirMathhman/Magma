// Auto-generated from magma/result/Results.java
export class Results {
	static unwrap<T, X extends Exception>(result: Result<T, X>): T {
		if (result.isOk()) {
			return ((Ok<T, X>) result).value();
		}
		Err<T, X> err = (Err<T, X>) result;
		throw new RuntimeException(err.error());
	}
}

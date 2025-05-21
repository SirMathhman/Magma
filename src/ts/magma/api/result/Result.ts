import { Option } from "../../../magma/api/option/Option";
export interface Result<T, X> {
	match<R>(whenOk: (arg0 : T) => R, whenErr: (arg0 : X) => R): R;
	flatMapValue<R>(mapper: (arg0 : T) => Result<R, X>): Result<R, X>;
	findError(): Option<X>;
	mapValue<R>(mapper: (arg0 : T) => R): Result<R, X>;
}

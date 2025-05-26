import { Result } from "../../../magma/api/result/Result";
import { Option } from "../../../magma/api/option/Option";
import { None } from "../../../magma/api/option/None";
export class Ok<T, X> implements Result<T, X> {
	value: T;
	constructor (value: T) {
		this.value = value;
	}
	match<R>(whenOk: (arg0 : T) => R, whenErr: (arg0 : X) => R): R {
		return whenOk(this.value)/*unknown*/;
	}
	flatMapValue<R>(mapper: (arg0 : T) => Result<R, X>): Result<R, X> {
		return mapper(this.value)/*unknown*/;
	}
	findError(): Option<X> {
		return new None<X>()/*unknown*/;
	}
	mapValue<R>(mapper: (arg0 : T) => R): Result<R, X> {
		return new Ok<R, X>(mapper(this.value))/*unknown*/;
	}
}

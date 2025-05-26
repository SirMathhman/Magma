import { Result } from "../../../magma/api/result/Result";
import { Option } from "../../../magma/api/option/Option";
import { Some } from "../../../magma/api/option/Some";
export class Err<T, X> implements Result<T, X> {
	error: X;
	constructor (error: X) {
		this.error = error;
	}
	match<R>(whenOk: (arg0 : T) => R, whenErr: (arg0 : X) => R): R {
		return whenErr(this.error)/*unknown*/;
	}
	flatMapNode<R>(mapper: (arg0 : T) => Result<R, X>): Result<R, X> {
		return new Err<R, X>(this.error)/*unknown*/;
	}
	findError(): Option<X> {
		return new Some<X>(this.error)/*unknown*/;
	}
	mapNode<R>(mapper: (arg0 : T) => R): Result<R, X> {
		return new Err<R, X>(this.error)/*unknown*/;
	}
}

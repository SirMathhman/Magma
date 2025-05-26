import { Iter } from "../../../magma/api/collect/Iter";
import { Option } from "../../../magma/api/option/Option";
import { HeadedIter } from "../../../magma/api/collect/head/HeadedIter";
import { EmptyHead } from "../../../magma/api/collect/head/EmptyHead";
import { Head } from "../../../magma/api/collect/head/Head";
import { SingleHead } from "../../../magma/api/collect/head/SingleHead";
import { RangeHead } from "../../../magma/api/collect/head/RangeHead";
export class Iters {
	static fromOption<T>(option: Option<T>): Iter<T> {
		return new HeadedIter<T>(option.map((element: T) => {
			return Iters.getTSingleHead(element)/*unknown*/;
		}).orElseGet(() => {
			return new EmptyHead<T>()/*unknown*/;
		}))/*unknown*/;
	}
	static getTSingleHead<T>(element: T): Head<T> {
		return new SingleHead<T>(element)/*unknown*/;
	}
	static fromArray<T>(): Iter<T> {
		return new HeadedIter<number>(new RangeHead(array.length)).map((index: number) => {
			/*return array[index]*/;
		})/*unknown*/;
	}
}

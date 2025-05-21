import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Option } from "../../../../magma/api/option/Option";
export interface Composable<T, R> {
	static toComposable<T>(mapper: (arg0 : string, arg1 : string) => Option<T>): Composable<Tuple2<string, string>, T> {
		return (tuple: Tuple2<string, string>) => mapper(tuple.left(), tuple.right())/*unknown*//*unknown*/;
	}
	apply(element: T): Option<R>;
}

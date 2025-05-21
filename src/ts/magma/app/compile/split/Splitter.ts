import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Option } from "../../../../magma/api/option/Option";
export interface Splitter {
	apply(input: string): Option<Tuple2<string, string>>;
}

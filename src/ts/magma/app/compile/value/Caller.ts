import { Value } from "../../../../magma/app/compile/value/Value";
import { Option } from "../../../../magma/api/option/Option";
export interface Caller {
	findChild(): Option<Value>;
}

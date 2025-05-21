import { IOError } from "../../magma/api/io/IOError";
import { Option } from "../../magma/api/option/Option";
import { Location } from "../../magma/app/Location";
export interface Targets {
	writeSource(location: Location, output: string): Option<IOError>;
}

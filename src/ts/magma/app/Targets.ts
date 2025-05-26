import { IOError } from "../../magma/api/io/IOError";
import { Option } from "../../magma/api/option/Option";
import { Location } from "../../magma/app/Location";
interface Targets {
	writeSource(location: Location, output: string): Option<IOError>;
}

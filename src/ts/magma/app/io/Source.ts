import { IOError } from "../../../magma/api/io/IOError";
import { Result } from "../../../magma/api/result/Result";
import { Location } from "../../../magma/app/Location";
export interface Source {
	read(): Result<string, IOError>;
	createLocation(): Location;
}

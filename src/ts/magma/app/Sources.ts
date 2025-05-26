import { Source } from "../../magma/app/io/Source";
import { Iterable } from "../../magma/api/collect/list/Iterable";
import { IOError } from "../../magma/api/io/IOError";
import { Result } from "../../magma/api/result/Result";
import { Path } from "../../magma/api/io/Path";
interface Sources {
	listSources(): Result<Iterable<Source>, IOError>;
	retainSources(children: Iterable<Path>): Iterable<Source>;
}

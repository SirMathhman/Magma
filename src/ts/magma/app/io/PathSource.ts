import { Source } from "../../../magma/app/io/Source";
import { Path } from "../../../magma/api/io/Path";
import { IOError } from "../../../magma/api/io/IOError";
import { Result } from "../../../magma/api/result/Result";
import { List } from "../../../magma/api/collect/list/List";
import { ListCollector } from "../../../magma/api/collect/list/ListCollector";
import { Location } from "../../../magma/app/Location";
export class PathSource implements Source {
	sourceDirectory: Path;
	source: Path;
	constructor (sourceDirectory: Path, source: Path) {
		this.sourceDirectory = sourceDirectory;
		this.source = source;
	}
	read(): Result<string, IOError> {
		return this.source.readString()/*unknown*/;
	}
	computeName(): string {
		let fileName = this.source.findFileName()/*unknown*/;
		let separator = fileName.lastIndexOf(".")/*unknown*/;
		return fileName.substring(0, separator)/*unknown*/;
	}
	computeNamespace(): List<string> {
		return this.sourceDirectory.relativize(this.source).getParent().iter().collect(new ListCollector<string>())/*unknown*/;
	}
	createLocation(): Location {
		return new Location(this.computeNamespace(), this.computeName())/*unknown*/;
	}
}

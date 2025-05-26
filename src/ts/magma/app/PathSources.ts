import { Sources } from "../../magma/app/Sources";
import { Path } from "../../magma/api/io/Path";
import { Source } from "../../magma/app/io/Source";
import { Iterable } from "../../magma/api/collect/list/Iterable";
import { IOError } from "../../magma/api/io/IOError";
import { Result } from "../../magma/api/result/Result";
import { PathSource } from "../../magma/app/io/PathSource";
import { ListCollector } from "../../magma/api/collect/list/ListCollector";
export class PathSources implements Sources {
	sourceDirectory: Path;
	constructor (sourceDirectory: Path) {
		this.sourceDirectory = sourceDirectory;
	}
	listSources(): Result<Iterable<Source>, IOError> {
		return this.sourceDirectory().walk().mapValue((children: Iterable<Path>) => {
			return this.retainSources(children)/*unknown*/;
		})/*unknown*/;
	}
	retainSources(children: Iterable<Path>): Iterable<Source> {
		return children.iter().filter((source: Path) => {
			return source.endsWith(".java")/*unknown*/;
		}). < Source > map((child: Path) => {
			return new PathSource(this.sourceDirectory, child)/*unknown*/;
		}).collect(new ListCollector<Source>())/*unknown*/;
	}
}

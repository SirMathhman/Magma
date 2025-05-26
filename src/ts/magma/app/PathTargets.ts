import { Targets } from "../../magma/app/Targets";
import { Path } from "../../magma/api/io/Path";
import { IOError } from "../../magma/api/io/IOError";
import { Option } from "../../magma/api/option/Option";
import { None } from "../../magma/api/option/None";
import { Location } from "../../magma/app/Location";
export class PathTargets implements Targets {
	root: Path;
	constructor (root: Path) {
		this.root = root;
	}
	static writeTarget(target: Path, output: string): Option<IOError> {
		return PathTargets.ensureTargetParent(target).or(() => {
			return target.writeString(output)/*unknown*/;
		})/*unknown*/;
	}
	static ensureTargetParent(target: Path): Option<IOError> {
		let parent = target.getParent()/*unknown*/;
		if (parent.exists()/*unknown*/){
			return new None<IOError>()/*unknown*/;
		}
		return parent.createDirectories()/*unknown*/;
	}
	writeSource(location: Location, output: string): Option<IOError> {
		let target = this.root.resolveChildSegments(location.namespace()).resolveChild(location.name() + ".ts")/*unknown*/;
		return PathTargets.writeTarget(target, output)/*unknown*/;
	}
}

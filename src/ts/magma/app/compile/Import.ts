import { Iterable } from "../../../magma/api/collect/list/Iterable";
import { Joiner } from "../../../magma/api/collect/Joiner";
import { Strings } from "../../../magma/api/text/Strings";
export class Import {
	namespace: Iterable<string>;
	child: string;
	constructor (namespace: Iterable<string>, child: string) {
		this.namespace = namespace;
		this.child = child;
	}
	generate(): string {
		let joinedNamespace = this.namespace.iter().collect(new Joiner("/")).orElse("")/*unknown*/;
		return "import { " + this.child + " } from \"" + joinedNamespace + "\";\n"/*unknown*/;
	}
	hasSameChild(child: string): boolean {
		return Strings.equalsTo(this.child, child)/*unknown*/;
	}
}

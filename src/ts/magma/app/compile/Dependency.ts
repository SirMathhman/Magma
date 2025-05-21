export class Dependency {
	name: string;
	child: string;
	constructor (name: string, child: string) {
		this.name = name;
		this.child = child;
	}
	toPlantUML(): string {
		return name() + " --> " + child() + "\n"/*unknown*/;
	}
}

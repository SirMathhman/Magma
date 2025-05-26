export class Dependency {
	name: string;
	child: string;
	constructor (name: string, child: string) {
		this.name = name;
		this.child = child;
	}
}

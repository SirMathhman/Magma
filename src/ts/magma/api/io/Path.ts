import { IOError } from "../../../magma/api/io/IOError";
import { Option } from "../../../magma/api/option/Option";
import { Result } from "../../../magma/api/result/Result";
import { Iterable } from "../../../magma/api/collect/list/Iterable";
import { Iter } from "../../../magma/api/collect/Iter";
export interface Path {
	writeString(output: string): Option<IOError>;
	readString(): Result<string, IOError>;
	resolveSibling(siblingName: string): Path;
	walk(): Result<Iterable<Path>, IOError>;
	findFileName(): string;
	endsWith(suffix: string): boolean;
	relativize(source: Path): Path;
	getParent(): Path;
	query(): Iter<string>;
	resolveChildSegments(children: Iterable<string>): Path;
	resolveChild(name: string): Path;
	exists(): boolean;
	createDirectories(): Option<IOError>;
}

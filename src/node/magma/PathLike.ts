// Auto-generated from magma/PathLike.java
import { Option } from "./option/Option";
import { Result } from "./result/Result";
export interface PathLike {
	resolve(other: string): PathLike;
	resolve(other: PathLike): PathLike;
	getParent(): PathLike;
	relativize(other: PathLike): PathLike;
	writeString(content: string): Option<IOException>;
	createDirectories(): Option<IOException>;
	exists(): boolean;
	readString(): Result<string, IOException>;
	streamNames(): Stream<string>;
	isRegularFile(): boolean;
}

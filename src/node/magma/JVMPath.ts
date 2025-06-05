// Auto-generated from magma/JVMPath.java
import { None } from "./option/None";
import { Option } from "./option/Option";
import { Some } from "./option/Some";
import { Err } from "./result/Err";
import { Ok } from "./result/Ok";
import { Result } from "./result/Result";
import { PathLike } from "./PathLike";
export class JVMPath implements PathLike {
	static of(first: string, more: String...): PathLike {
		return new JVMPath(Path.of(first, more));
	}
	toString(): string {
		return path.toString();
	}
	resolve(other: string): PathLike {
		return new JVMPath(path.resolve(other));
	}
	resolve(other: PathLike): PathLike {
		return new JVMPath(path.resolve(toPath(other)));
	}
	toPath(other: PathLike): Path {
		const names: var = other.streamNames().toList();
		if (names.isEmpty()) {
		return Paths.get("");
		const first: var = Paths.get(names.getFirst());
		return names.subList(1, names.size())
		.reduce(first, Path::resolve, (_, next) -> next);
	}
	getParent(): PathLike {
		let parent: Path = path.getParent();
		return new JVMPath(parent);
	}
	relativize(other: PathLike): PathLike {
		return new JVMPath(path.relativize(toPath(other)));
	}
	writeString(content: string): Option<IOException> {
		Files.writeString(path, content);
		return new None<>();
		return new Some<>(e);
	}
	createDirectories(): Option<IOException> {
		Files.createDirectories(path);
		return new None<>();
		return new Some<>(e);
	}
	exists(): boolean {
		return Files.exists(path);
	}
	readString(): Result<string, IOException> {
		return new Ok<>(Files.readString(path));
		return new Err<>(e);
	}
	streamNames(): Stream<string> {
		const root: var = path.getRoot();
		const rootStream: Stream<string> = root == null ? Stream.empty() : Stream.of(root.toString());
		const namesStream: var = IntStream.range(0, path.getNameCount())
		.map(Path::toString);
		return Stream.concat(rootStream, namesStream);
	}
	isRegularFile(): boolean {
		return Files.isRegularFile(path);
	}
}

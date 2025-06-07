import IOException from "../../java/io/IOException";
import Files from "../../java/nio/file/Files";
import Path from "../../java/nio/file/Path";
import LinkedHashSet from "../../java/util/LinkedHashSet";
import Set from "../../java/util/Set";
import Err from "../result/Err";
import Ok from "../result/Ok";
import Result from "../result/Result";
/**
 * Implementation of {@link PathLike} that delegates to a
 * {@link Path} instance.
 */
export default class NioPath implements PathLike {
    private readonly path: Path;

    NioPath(path: Path): private {
        // TODO
    }

    /** Create a wrapper from path segments. */
    of(first: string): NioPath {
        return new NioPath(Path.of(first));
    }

    /** Wrap an existing NIO path. */
    wrap(path: Path): NioPath {
        return new NioPath(path);
    }


    /** Read the file contents as a string. */
    readString(): string {
        // TODO
        throw new Error("NotImplemented");
    }

    /** Create this directory and any missing parents. */
    createDirectories(): void {
        // TODO
    }

    /** Write text to this file. */
    writeString(text: string): void {
        // TODO
    }

    /** Delete the file if it exists. */
    deleteIfExists(): void {
        // TODO
    }

    @Override
    resolve(other: string): PathLike {
        return new NioPath(path.resolve(other));
    }

    @Override
    relativize(other: PathLike): PathLike {
        return new NioPath(path.relativize(((NioPath) other).path));
    }

    @Override
    getParent(): PathLike {
        let parent: var = path.getParent();
        return parent == null ? null : new NioPath(parent);
    }

    @Override
    walk(): Result<Set<PathLike>> {
        let out: Set<PathLike> = new LinkedHashSet<>();
        let stream: (var = Files./* TODO */;
        stream.forEach(p => out.add(new NioPath(p)));
        return new Ok<>(out);
        } catch(/* TODO */);
        return new Err<>(e.getMessage());
        // TODO
    }

    @Override
    toString(): string {
        return path.toString();
    }
}

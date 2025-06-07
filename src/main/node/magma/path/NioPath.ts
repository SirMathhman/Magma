import Path from "../../java/nio/file/Path";
/**
 * Implementation of {@link PathLike} that delegates to a
 * {@link java.nio.file.Path} instance.
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
        let parent: Path = path.getParent();
        return parent == null ? null : new NioPath(parent);
    }

    @Override
    toString(): string {
        return path.toString();
    }
}

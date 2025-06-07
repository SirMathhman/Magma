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

    toNio(): Path {
        return path;
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

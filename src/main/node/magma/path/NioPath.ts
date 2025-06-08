import Files from "../../java/nio/file/Files";
import Path from "../../java/nio/file/Path";
import LinkedHashSet from "../../java/util/LinkedHashSet";
import Set from "../../java/util/Set";
import Err from "../result/Err";
import Ok from "../result/Ok";
import Result from "../result/Result";
import Option from "../option/Option";
import Some from "../option/Some";
import None from "../option/None";
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
    @Override
    readString(): Result<string> {
        // TODO
        return new Ok<>(Files.readString(path));
        } catch(java.io.IOException e);
        return new Err<>(e.getMessage());
        // TODO
    }

    /** Create this directory and any missing parents. */
    @Override
    createDirectories(): Option<string> {
        // TODO
        Files.createDirectories(path);
        return new None<>();
        } catch(java.io.IOException e);
        return new Some<>(e.getMessage());
        // TODO
    }

    /** Write text to this file. */
    @Override
    writeString(text: string): Option<string> {
        // TODO
        Files.writeString(path, text);
        return new None<>();
        } catch(java.io.IOException e);
        return new Some<>(e.getMessage());
        // TODO
    }

    /** Delete the file if it exists. */
    @Override
    deleteIfExists(): Option<string> {
        // TODO
        Files.deleteIfExists(path);
        return new None<>();
        } catch(java.io.IOException e);
        return new Some<>(e.getMessage());
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
        let parent : unknown = path.getParent();
        return parent == null ? null : new NioPath(parent);
    }

    @Override
    walk(): Result<Set<PathLike>> {
        let out : Set<PathLike> = new LinkedHashSet<>();
        let stream : (var = Files./* TODO */;
        stream.forEach(p => out.add(new NioPath(p)));
        return new Ok<>(out);
        } catch(java.io.IOException e);
        return new Err<>(e.getMessage());
        // TODO
    }

    @Override
    toString(): string {
        return path.toString();
    }
}

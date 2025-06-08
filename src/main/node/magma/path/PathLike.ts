import Set from "../../java/util/Set";
import Result from "../result/Result";
import Option from "../option/Option";
/**
 * Minimal abstraction over file system paths. This wrapper lets the
 * rest of the code avoid a hard dependency on {@code java.nio.file.Path}.
 */
export interface PathLike {
    resolve(other : string): PathLike;
    relativize(other : PathLike): PathLike;
    getParent(): PathLike;
    walk(): Result<Set<PathLike>>;
    readString(): Result<string>;
    createDirectories(): Option<string>;
    writeString(text : string): Option<string>;
    deleteIfExists(): Option<string>;
    toString(): string;
}

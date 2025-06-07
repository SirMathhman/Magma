import Transpiler from "./app/Transpiler";
import None from "./option/None";
import Option from "./option/Option";
import Some from "./option/Some";
import Err from "./result/Err";
import Ok from "./result/Ok";
import Result from "./result/Result";
import IOException from "../java/io/IOException";
import Files from "../java/nio/file/Files";
import Path from "../java/nio/file/Path";
import ArrayList from "../java/util/ArrayList";
import List from "../java/util/List";
/**
 * Simple command line interface for the Transpiler.
 */
export default class Main {
    main(args: string[]): void {
        let error: Option<string> = new Main().run();
        if (/* TODO */) {
            // TODO
        }
    }

    run(): Option<string> {
        let srcRoot: any = /* TODO */("src/main/java");
        let outRoot: any = /* TODO */("src/main/node");
        let files: Result<List<any>> = /* TODO */(/* TODO */);
        if (/* TODO */) {
            // TODO
        }
        // TODO
        let err: Option<string> = /* TODO */(/* TODO */, /* TODO */, /* TODO */);
        if (/* TODO */) {
            // TODO
        }
        // TODO
        return /* TODO */;
    }

    listJavaFiles(srcRoot: any): Result<List<any>> {
        let javaFiles: List<any> = new ArrayList<>();
        let stream: any = /* TODO */;
        // TODO
        if (/* TODO */) {
            // TODO
        }
        // TODO
        return /* TODO */;
        /* TODO */(/* TODO */);
        return /* TODO */;
        // TODO
    }

    transpileFile(srcRoot: any, outRoot: any, javaFile: any): Option<string> {
        // TODO
        let javaSrc: string = /* TODO */(/* TODO */);
        let ts: string = new Transpiler().toTypeScript(javaSrc);
        let rel: any = /* TODO */(/* TODO */);
        let name: string = /* TODO */();
        let withoutExt: string = /* TODO */(/* TODO */, /* TODO */);
        let outFile: any = /* TODO */(withoutExt + ".ts");
        /* TODO */(/* TODO */);
        /* TODO */(/* TODO */, /* TODO */);
        return /* TODO */;
        /* TODO */(/* TODO */);
        return /* TODO */;
        // TODO
    }
}

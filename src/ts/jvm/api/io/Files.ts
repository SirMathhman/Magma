import { Path } from "../../../magma/api/io/Path";
export interface FilesInstance {
	get(first: string, ...more: string[]): Path;

}
export declare const Files: FilesInstance;

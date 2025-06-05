// Auto-generated from magma/TypeScriptStubs.java
import { None } from "./option/None";
import { Option } from "./option/Option";
import { Some } from "./option/Some";
import { Err } from "./result/Err";
import { JavaFile } from "./JavaFile";
import { Results } from "./result/Results";
export class TypeScriptStubs {
	static write(javaRoot: PathLike, tsRoot: PathLike): Option<IOException> {
		return javaRoot.walk().match(stream => {
		let files: List<PathLike> = stream.filter(PathLike::isRegularFile)
		.toList();
		for (PathLike file : files) {
		let res: Option<IOException> = processFile(javaRoot, tsRoot, file);
		if (res.isPresent()) {
		return res;
		return new None<>();
	}
	static processFile(javaRoot: PathLike, tsRoot: PathLike, file: PathLike): Option<IOException> {
		let relative: PathLike = javaRoot.relativize(file);
		let tsFile: PathLike = tsRoot.resolve(relative.toString().replaceFirst("\\.java$", ".ts"));
		let dirResult: var = tsFile.getParent().createDirectories();
		if (dirResult.isPresent()) {
		return dirResult;
		let jf: JavaFile = new JavaFile(file);
		let importsRes: var = jf.imports();
		if (importsRes.isErr()) {
		return new Some<>(((Err<List<String>, IOException>) importsRes).error());
		let pkgRes: var = jf.packageName();
		if (pkgRes.isErr()) {
		return new Some<>(((Err<String, IOException>) pkgRes).error());
		let localRes: var = jf.localDependencies();
		if (localRes.isErr()) {
		return new Some<>(((Err<List<String>, IOException>) localRes).error());
		let declarationsRes: var = jf.declarations();
		if (declarationsRes.isErr()) {
		return new Some<>(((Err<List<String>, IOException>) declarationsRes).error());
		let methodsRes: var = jf.methods();
		if (methodsRes.isErr()) {
		return new Some<>(((Err<Map<String, List<String>>, IOException>) methodsRes).error());
		let imports: List<string> = Results.unwrap(importsRes);
		let pkgName: string = Results.unwrap(pkgRes);
		let locals: List<string> = Results.unwrap(localRes);
		mergeLocalImports(imports, locals, pkgName);
		let declarations: List<string> = Results.unwrap(declarationsRes);
		Map<String, List<String>> methods = Results.unwrap(methodsRes);
		let content: string = stubContent(relative, tsFile.getParent(), tsRoot,
		let writeRes: var = tsFile.writeString(content);
		if (writeRes.isPresent()) {
		return writeRes;
		return new None<>();
	}
	JavaFile(): new;
	static mergeLocalImports(imports: List<string>, locals: List<string>, pkgName: string): void {
		for (String dep : locals) {
		let fqn: string = pkgName.isEmpty() ? dep : pkgName + "." + dep;
		if (!imports.contains(fqn)) {
		imports.add(fqn);
	}
	static stubContent(relative: PathLike, from: PathLike, root: PathLike, imports: List<string>, declarations: List<string>, methods: Map<string, List<string>>): string {
		let builder: StringBuilder = new StringBuilder();
		Map<String, List<String>> byPath = groupImports(imports, from, root);
		appendImportLines(builder, byPath);
		if (declarations.isEmpty()) {
		builder.append("export {};").append(System.lineSeparator());
		return builder.toString();
		appendDeclarations(builder, relative, declarations, methods);
		return builder.toString();
	}
	StringBuilder(): new;
	static appendImportLines(builder: StringBuilder, byPath: Map<string, List<string>>): void {
		for (var entry : byPath.entrySet()) {
		builder.append("import { ");
		builder.append(String.join(", ", entry.getValue()));
		builder.append(" } from \"").append(entry.getKey()).append("\"");
		builder.append(";").append(System.lineSeparator());
	}
	static appendDeclarations(builder: StringBuilder, relative: PathLike, declarations: List<string>, methods: Map<string, List<string>>): void {
		let namePattern: var = Pattern.compile("export \\w+ (\\w+)(?:<[^>]+>)?");
		let isMain: boolean = relative.toString().replace('\\', '/').equals("magma/Main.java");
		for (String decl : declarations) {
		appendDeclaration(builder, decl, namePattern, methods);
		if (isMain) {
		builder.append("Main.main([]);").append(System.lineSeparator());
	}
	static appendDeclaration(builder: StringBuilder, decl: string, namePattern: Pattern, methods: Map<string, List<string>>): void {
		let m: var = namePattern.matcher(decl);
		if (!m.find()) {
		builder.append(decl).append(System.lineSeparator());
		return;
		let name: string = m.group(1);
		let mList: List<string> = methods.getOrDefault(name, Collections.emptyList());
		if (mList.isEmpty()) {
		builder.append(decl).append(System.lineSeparator());
		return;
		builder.append(decl, 0, decl.length() - 1).append(System.lineSeparator());
		for (String method : mList) {
		builder.append(method).append(System.lineSeparator());
	}
}

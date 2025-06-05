// Auto-generated from magma/GenerateDiagram.java
import { Option } from "./option/Option";
import { Some } from "./option/Some";
export class GenerateDiagram {
	static writeDiagram(output: PathLike): Option<IOException> {
		let src: PathLike = JVMPath.of("src/java/magma");
                return Sources.read(src).match(allSources => {
		let analysis: Sources = new Sources(allSources);
		let classes: List<string> = analysis.findClasses();
		let implementations: var = analysis.findImplementations();
		let sourceMap: var = analysis.mapSourcesByClass();
		let content: string = "@startuml\n" + "skinparam linetype ortho\n" +
		return output.writeString(content);
	}
	Sources(): new;
	static classesSection(classes: List<string>, sourceMap: Map<string, string>): string {
		let builder: StringBuilder = new StringBuilder();
		for (String name : classes) {
		let source: string = sourceMap.getOrDefault(name, "");
		let type: string = classType(name, source);
		builder.append(type).append(' ').append(name).append("\n");
		return builder.toString();
	}
	StringBuilder(): new;
	static classType(name: string, source: string): string {
		let pattern: Pattern = Pattern.compile(
		"(class|interface|record)\\s+" + Pattern.quote(name) + "\\b");
		let matcher: Matcher = pattern.matcher(source);
		if (matcher.find()) {
		let kind: string = matcher.group(1);
		if ("interface".equals(kind)) {
		return "interface";
		return "class";
	}
}

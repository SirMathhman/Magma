// Auto-generated from magma/Sources.java
import { Err } from "./result/Err";
import { Ok } from "./result/Ok";
import { Result } from "./result/Result";
import { PathLike } from "./PathLike";
export class Sources {
	list: List<string>;
	constructor(list: List<string>) {
		this.list = list;
	}
	findClasses(): List<string> {
		let pattern: Pattern = Pattern.compile(
		"(?:class|interface|record)\\s+(\\w+)",
		let unique: Set<string> = new LinkedHashSet<>();
		for (String src : list) {
			unique.addAll(classesFromSource(src, pattern));
		}
		let names: List<string> = new ArrayList<>(unique);
		Collections.sort(names);
		return names;
	}
	findInheritanceRelations(): List<Relation> {
		let extendsPattern: Pattern = Pattern.compile(
		+ "(?:class|interface|record)\\s+(\\w+)(?:\\([^)]*\\))?"
		let implementsPattern: Pattern = Pattern.compile(
		+ "(?:class|record)\\s+(\\w+)(?:\\([^)]*\\))?"
		let relations: List<Relation> = new ArrayList<>();
		for (String src : list) {
			src = src.replaceAll("<[^>]*>", "");
			src = stripComments(src);
			relations.addAll(inheritanceFromSource(src, extendsPattern));
			relations.addAll(inheritanceFromSource(src, implementsPattern));
		}
		return relations;
	}
	mapSourcesByClass(): Map<string, string> {
		Map<String, String> map = new java.util.HashMap<>();
		let classPattern: Pattern = Pattern.compile("(?:class|interface|record)\\s+(\\w+)");
		for (String src : list) {
			let stripped: string = stripComments(src);
			let matcher: Matcher = classPattern.matcher(stripped);
			if (matcher.find()) {
				map.put(matcher.group(1), stripped);
			}
		}
		return map;
	}
	findDependencyRelations(classes: List<string>, inheritance: List<Relation>, implementations: Map<string, List<string>>): List<Relation> {
		let classPattern: Pattern = Pattern.compile("(?:class|interface|record)\\s+(\\w+)");
		Map<String, String> sourceMap = mapSourcesByClass();
		let inherited: Set<string> = toInheritedSet(inheritance);
		let relations: List<Relation> = new ArrayList<>();
		for (String src : list) {
		}
		return relations;
	}
	findRelations(classes: List<string>, implementations: Map<string, List<string>>): List<Relation> {
		let inheritance: List<Relation> = findInheritanceRelations();
		let dependencies: List<Relation> = findDependencyRelations(classes, inheritance, implementations);
		let all: Set<Relation> = new LinkedHashSet<>();
		all.addAll(inheritance);
		all.addAll(dependencies);
		return new ArrayList<>(all);
	}
	static classesFromSource(src: string, pattern: Pattern): Set<string> {
		let result: Set<string> = new LinkedHashSet<>();
		let matcher: Matcher = pattern.matcher(src);
		while (matcher.find()) {
			result.add(matcher.group(1));
		}
		return result;
	}
	static inheritanceFromSource(src: string, pattern: Pattern): List<Relation> {
		let result: List<Relation> = new ArrayList<>();
		let matcher: Matcher = pattern.matcher(src);
		while (matcher.find()) {
			let child: string = matcher.group(1);
			let parents: string = matcher.group(2);
			result.addAll(parentRelations(child, parents));
		}
		return result;
	}
	static parentRelations(child: string, parents: string): List<Relation> {
		let relations: List<Relation> = new ArrayList<>();
		for (String parent : parents.split(",")) {
			parent = parent.replaceAll("<.*?>", "").trim();
			if (!parent.isEmpty()) {
				relations.add(new Relation(child, "--|>", parent));
			}
		}
		return relations;
	}
	static parseInterfaces(parents: string): List<string> {
		let interfaces: List<string> = new ArrayList<>();
		for (String parent : parents.split(",")) {
			parent = parent.replaceAll("<.*?>", "").trim();
			if (!parent.isEmpty()) {
				interfaces.add(parent);
			}
		}
		return interfaces;
	}
	static stripComments(src: string): string {
		src = src.replaceAll("(?s)/\\*.*?\\*/", "");
		src = src.replaceAll("
		return src;
	}
	static stripStrings(src: string): string {
		return src.replaceAll("\"(?:\\\\.|[^\"\\\\])*\"", "");
	}
	static toInheritedSet(inheritance: List<Relation>): Set<string> {
		let set: Set<string> = new LinkedHashSet<>();
		for (Relation rel : inheritance) {
			set.add(rel.from() + "=>" + rel.to());
		}
		return set;
	}
	static containsReference(source: string, names: List<string>): boolean {
		for (String name : names) {
			let word: Pattern = Pattern.compile("\\b" + Pattern.quote(name) + "\\b");
			if (word.matcher(source).find()) {
				return true;
			}
		}
		return false;
	}
	static dependenciesForSource(src: string, classPattern: Pattern, classes: List<string>, inherited: Set<string>, sourceMap: Map<string, string>, implementations: Map<string, List<string>>): List<Relation> {
		let relations: List<Relation> = new ArrayList<>();
		src = stripComments(src);
		src = stripStrings(src);
		let matcher: Matcher = classPattern.matcher(src);
		if (!matcher.find()) {
			return relations;
		}
		let name: string = matcher.group(1);
		Map<String, List<String>> byInterface = invertImplementations(implementations);
		for (String other : classes) {
			if (other.equals(name)) {
			}
			let word: Pattern = Pattern.compile("\\b" + Pattern.quote(other) + "\\b");
			if (!word.matcher(src).find()) {
			}
			if (inherited.contains(name + "=>" + other)) {
			}
			let otherIfaces: List<string> = implementations.get(other);
			if (otherIfaces != null && otherIfaces.contains(name)) {
			}
			let impls: List<string> = byInterface.get(other);
			if (impls != null && containsReference(src, impls)) {
			}
			relations.add(new Relation(name, "-=>", other));
		}
		return relations;
	}
	formatRelations(classes: List<string>, implementations: Map<string, List<string>>): string {
		let builder: StringBuilder = new StringBuilder();
		for (Relation rel : findRelations(classes, implementations)) {
			.append(rel.to()).append("\n");
		}
		return builder.toString();
	}
	StringBuilder(): new;
}

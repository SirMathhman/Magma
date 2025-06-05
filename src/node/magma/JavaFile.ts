// Auto-generated from magma/JavaFile.java
import { Err } from "./result/Err";
import { Ok } from "./result/Ok";
import { Result } from "./result/Result";
export class JavaFile {
	file: PathLike;
	constructor(file: PathLike) {
		this.file = file;
	}
	static Param(name: string, tsType: string): record {
		return 0;
	}
	packageName(): Result<string, IOException> {
		return file.readString().mapValue(source => {
			let pattern: var = Pattern.compile("^package\\s+([\\w.]+);
			let matcher: var = pattern.matcher(source);
			if (matcher.find()) {
				return matcher.group(1);
			}
			return "";
		}
	}
	if(-1: extIdx !=): else {
		extendsPart = rest.substring(extIdx + 8).trim();
	}
	static addParts(clause: string, deps: List<string>, defined: List<string>): void {
		if (clause == null || clause.isEmpty()) {
			return;
		}
		clause = clause.replaceAll("<.*?>", "");
		for (String part : clause.split(",")) {
			let base: string = part.trim();
			if (!base.isEmpty() && !defined.contains(base)) {
				deps.add(base);
			}
		}
	}
	StringBuilder(name: "export " + kind + " " +): new;
	static extractClassBody(source: string, start: number): string {
		let level: number = 1;
		let i: number = start;
		while (i < source.length() && level > 0) {
			let ch: string = source.charAt(i);
			if (ch == '{
				}
				else if (ch == '}
				}
			}
			return source.substring(start, i - 1);
	}
	if('}': ch ==): else {
		return 0;
	}
	static parseMethods(body: string, className: string, isInterface: boolean): List<string> {
		let methodPat: var = Pattern.compile(
			let mMatcher: var = methodPat.matcher(body);
			let list: List<string> = new ArrayList<>();
			while (mMatcher.find()) {
				let mName: string = mMatcher.group(4);
				if (mName.equals(className)) {
				}
				let delim: string = mMatcher.group(6);
				let methodBody: string = "";
				if ("{
						let start: number = mMatcher.end();
						methodBody = extractBlock(body, start);
					}
				}
				return list;
			}
				let prefix: string = staticKw == null ? "" : "static ";
				let typeParams: string = generics == null ? "" : generics.trim();
				let paramList: string = tsParams(params);
				if (isInterface || ";
					return;
				}
					let segs: List<string> = MethodBodyParser.parseSegments(body);
					if (segs.isEmpty()) {
					}
					else {
						for (String seg : segs) {
							list.add("\t\t" + seg);
						}
					}
				}
				private static String tsParams(String javaParams) {
					javaParams = javaParams.trim();
					let out: StringBuilder = new StringBuilder();
					let depth: number = 0;
					let start: number = 0;
					let first: boolean = true;
					for (int i = 0;
					i <= javaParams.length();
						let atEnd: boolean = i == javaParams.length();
						let atComma: boolean = !atEnd && javaParams.charAt(i) == ',' && depth == 0;
						if (atEnd || atComma) {
							let part: string = javaParams.substring(start, i).trim();
							first = appendParam(part, out, first);
							start = i + 1;
						}
						if (javaParams.charAt(i) == '<') {
						}
						else if (javaParams.charAt(i) == '>') {
						}
					}
					return out.toString();
				}
				private static boolean appendParam(String part, StringBuilder out, boolean first) {
					if (part.isEmpty()) {
						return first;
					}
					let last: number = part.lastIndexOf(' ');
					if (last == -1) {
						return first;
					}
					let type: string = part.substring(0, last).trim();
					let name: string = part.substring(last + 1).trim();
					if (!first) {
						out.append(", ");
					}
					out.append(name).append(": ").append(tsType(type));
					return false;
				}
				private static List<Param> parseRecordParams(String javaParams) {
					javaParams = javaParams.trim();
					let list: List<Param> = new ArrayList<>();
					let depth: number = 0;
					let start: number = 0;
					for (int i = 0;
					i <= javaParams.length();
						let atEnd: boolean = i == javaParams.length();
						let atComma: boolean = !atEnd && javaParams.charAt(i) == ',' && depth == 0;
						if (atEnd || atComma) {
							let part: string = javaParams.substring(start, i).trim();
							if (!part.isEmpty()) {
								let last: number = part.lastIndexOf(' ');
								if (last != -1) {
									let type: string = part.substring(0, last).trim();
									let name: string = part.substring(last + 1).trim();
									list.add(new Param(name, tsType(type)));
								}
							}
							start = i + 1;
						}
						if (javaParams.charAt(i) == '<') {
						}
						else if (javaParams.charAt(i) == '>') {
						}
					}
					return list;
				}
				static String tsType(String javaType) {
					javaType = javaType.trim();
					if (javaType.endsWith("[]")) {
						let inner: string = javaType.substring(0, javaType.length() - 2);
						return tsType(inner) + "[]";
					}
					let lt: number = javaType.indexOf('<');
					if (lt != -1 && javaType.endsWith(">")) {
						let base: string = javaType.substring(0, lt);
						let args: string = javaType.substring(lt + 1, javaType.length() - 1);
						let converted: List<string> = convertTypes(splitGenericArgs(args));
						converted.replaceAll(JavaFile::sanitizeWildcard);
						let simple: string = base.replace("java.util.function.", "");
						if ("Function".equals(simple) && converted.size() >= 2) {
							return "(arg0: " + converted.get(0) + ") = > " + converted.get(1);
						}
						if ("BiFunction".equals(simple) && converted.size() >= 3) {
							return "(arg0: " + converted.get(0) + ", arg1: " + converted.get(1)
							+ ") = > " + converted.get(2);
						}
						if ("Supplier".equals(simple) && !converted.isEmpty()) {
							return "() = > " + converted.getFirst();
						}
						if ("Consumer".equals(simple) && !converted.isEmpty()) {
							return "(arg0: " + converted.getFirst() + ") = > void";
						}
						if ("BiConsumer".equals(simple) && converted.size() >= 2) {
							return "(arg0: " + converted.get(0) + ", arg1: " + converted.get(1)
							+ ") = > void";
						}
						if ("Predicate".equals(simple) && !converted.isEmpty()) {
							return "(arg0: " + converted.getFirst() + ") = > boolean";
						}
						return base + "<" + String.join(", ", converted) + ">";
					}
					return switch (javaType) {
					}
				}
				private static List<String> splitGenericArgs(String args) {
					let parts: List<string> = new ArrayList<>();
					let depth: number = 0;
					let start: number = 0;
					for (int i = 0;
					i < args.length();
						let ch: string = args.charAt(i);
						if (ch == '<') {
						}
						else if (ch == '>') {
						}
						else if (ch == ',' && depth == 0) {
							parts.add(args.substring(start, i).trim());
							start = i + 1;
						}
					}
					parts.add(args.substring(start).trim());
					return parts;
				}
				private static List<String> convertTypes(List<String> parts) {
					let converted: List<string> = new ArrayList<>();
					for (String part : parts) {
						converted.add(tsType(part));
					}
					return converted;
				}
				private static String extractBlock(String source, int start) {
					let level: number = 1;
					let i: number = start;
					while (i < source.length() && level > 0) {
						let ch: string = source.charAt(i);
						if (ch == '{
							}
							else if (ch == '}
							}
						}
						return source.substring(start, i - 1);
					}
					private static String sanitizeWildcard(String type) {
						type = type.trim();
						if (type.startsWith("? extends ")) {
							return type.substring(10).trim();
						}
						if (type.startsWith("? super ")) {
							return type.substring(8).trim();
						}
						if ("?".equals(type)) {
							return "any";
						}
						return type;
					}
	}
	static addMethod(list: List<string>, staticKw: string, generics: string, returnType: string, name: string, params: string, delim: string, isInterface: boolean, body: string): void {
		let prefix: string = staticKw == null ? "" : "static ";
		let typeParams: string = generics == null ? "" : generics.trim();
		let paramList: string = tsParams(params);
		if (isInterface || ";
			return;
		}
			let segs: List<string> = MethodBodyParser.parseSegments(body);
			if (segs.isEmpty()) {
			}
			else {
				for (String seg : segs) {
					list.add("\t\t" + seg);
				}
			}
	}
	static tsParams(javaParams: string): string {
		javaParams = javaParams.trim();
		let out: StringBuilder = new StringBuilder();
		let depth: number = 0;
		let start: number = 0;
		let first: boolean = true;
		for (int i = 0;
		i <= javaParams.length();
			let atEnd: boolean = i == javaParams.length();
			let atComma: boolean = !atEnd && javaParams.charAt(i) == ',' && depth == 0;
			if (atEnd || atComma) {
				let part: string = javaParams.substring(start, i).trim();
				first = appendParam(part, out, first);
				start = i + 1;
			}
			if (javaParams.charAt(i) == '<') {
			}
			else if (javaParams.charAt(i) == '>') {
			}
		}
		return out.toString();
	}
	StringBuilder(): new;
	static appendParam(part: string, out: StringBuilder, first: boolean): boolean {
		if (part.isEmpty()) {
			return first;
		}
		let last: number = part.lastIndexOf(' ');
		if (last == -1) {
			return first;
		}
		let type: string = part.substring(0, last).trim();
		let name: string = part.substring(last + 1).trim();
		if (!first) {
			out.append(", ");
		}
		out.append(name).append(": ").append(tsType(type));
		return false;
	}
	static parseRecordParams(javaParams: string): List<Param> {
		javaParams = javaParams.trim();
		let list: List<Param> = new ArrayList<>();
		let depth: number = 0;
		let start: number = 0;
		for (int i = 0;
		i <= javaParams.length();
			let atEnd: boolean = i == javaParams.length();
			let atComma: boolean = !atEnd && javaParams.charAt(i) == ',' && depth == 0;
			if (atEnd || atComma) {
				let part: string = javaParams.substring(start, i).trim();
				if (!part.isEmpty()) {
					let last: number = part.lastIndexOf(' ');
					if (last != -1) {
						let type: string = part.substring(0, last).trim();
						let name: string = part.substring(last + 1).trim();
						list.add(new Param(name, tsType(type)));
					}
				}
				start = i + 1;
			}
			if (javaParams.charAt(i) == '<') {
			}
			else if (javaParams.charAt(i) == '>') {
			}
		}
		return list;
	}
	static tsType(javaType: string): string {
		javaType = javaType.trim();
		if (javaType.endsWith("[]")) {
			let inner: string = javaType.substring(0, javaType.length() - 2);
			return tsType(inner) + "[]";
		}
		let lt: number = javaType.indexOf('<');
		if (lt != -1 && javaType.endsWith(">")) {
			let base: string = javaType.substring(0, lt);
			let args: string = javaType.substring(lt + 1, javaType.length() - 1);
			let converted: List<string> = convertTypes(splitGenericArgs(args));
			converted.replaceAll(JavaFile::sanitizeWildcard);
			let simple: string = base.replace("java.util.function.", "");
			if ("Function".equals(simple) && converted.size() >= 2) {
				return "(arg0: " + converted.get(0) + ") = > " + converted.get(1);
			}
			if ("BiFunction".equals(simple) && converted.size() >= 3) {
				return "(arg0: " + converted.get(0) + ", arg1: " + converted.get(1)
				+ ") = > " + converted.get(2);
			}
			if ("Supplier".equals(simple) && !converted.isEmpty()) {
				return "() = > " + converted.getFirst();
			}
			if ("Consumer".equals(simple) && !converted.isEmpty()) {
				return "(arg0: " + converted.getFirst() + ") = > void";
			}
			if ("BiConsumer".equals(simple) && converted.size() >= 2) {
				return "(arg0: " + converted.get(0) + ", arg1: " + converted.get(1)
				+ ") = > void";
			}
			if ("Predicate".equals(simple) && !converted.isEmpty()) {
				return "(arg0: " + converted.getFirst() + ") = > boolean";
			}
			return base + "<" + String.join(", ", converted) + ">";
		}
		return switch (javaType) {
		}
	}
	switch(): return {
		return 0;
	}
	static splitGenericArgs(args: string): List<string> {
		let parts: List<string> = new ArrayList<>();
		let depth: number = 0;
		let start: number = 0;
		for (int i = 0;
		i < args.length();
			let ch: string = args.charAt(i);
			if (ch == '<') {
			}
			else if (ch == '>') {
			}
			else if (ch == ',' && depth == 0) {
				parts.add(args.substring(start, i).trim());
				start = i + 1;
			}
		}
		parts.add(args.substring(start).trim());
		return parts;
	}
	if('>': ch ==): else {
		return 0;
	}
	if(': ch ==, 0: ' && depth ==): else {
		parts.add(args.substring(start, i).trim());
		start = i + 1;
	}
	static convertTypes(parts: List<string>): List<string> {
		let converted: List<string> = new ArrayList<>();
		for (String part : parts) {
			converted.add(tsType(part));
		}
		return converted;
	}
	static extractBlock(source: string, start: number): string {
		let level: number = 1;
		let i: number = start;
		while (i < source.length() && level > 0) {
			let ch: string = source.charAt(i);
			if (ch == '{
				}
				else if (ch == '}
				}
			}
			return source.substring(start, i - 1);
	}
	if('}': ch ==): else {
		return 0;
	}
	static sanitizeWildcard(type: string): string {
		type = type.trim();
		if (type.startsWith("? extends ")) {
			return type.substring(10).trim();
		}
		if (type.startsWith("? super ")) {
			return type.substring(8).trim();
		}
		if ("?".equals(type)) {
			return "any";
		}
		return type;
	}
}

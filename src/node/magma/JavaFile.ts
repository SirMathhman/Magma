// Auto-generated from magma/JavaFile.java
import { Err } from "./result/Err";
import { Ok } from "./result/Ok";
import { Result } from "./result/Result";
export class JavaFile {
	packageName(): Result<string, IOException> {
                return file.readString().mapValue(source => {
		let pattern: var = Pattern.compile("^package\\s+([\\w.]+);", Pattern.MULTILINE);
		let matcher: var = pattern.matcher(source);
		if (matcher.find()) {
		return matcher.group(1);
		return "";
	}
	if(-1: extIdx !=): else {
		extendsPart = rest.substring(extIdx + 8).trim();
	}
	static addParts(clause: string, deps: List<string>, defined: List<string>): void {
		if (clause == null || clause.isEmpty()) {
		return;
		clause = clause.replaceAll("<.*?>", "");
		for (String part : clause.split(",")) {
		let base: string = part.trim();
		if (!base.isEmpty() && !defined.contains(base)) {
		deps.add(base);
	}
	StringBuilder(name: "export " + kind + " " +): new;
	static extractClassBody(source: string, start: number): string {
		let level: number = 1;
		let i: number = start;
		while (i < source.length() && level > 0) {
		let ch: string = source.charAt(i);
		if (ch == '{') {
		else if (ch == '}') {
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
		let delim: string = mMatcher.group(6);
		let methodBody: string = "";
		if ("{".equals(delim)) {
		let start: number = mMatcher.end();
		methodBody = extractBlock(body, start);
		return list;
		let prefix: string = staticKw == null ? "" : "static ";
		let typeParams: string = generics == null ? "" : generics.trim();
		let paramList: string = tsParams(params);
		if (isInterface || ";".equals(delim)) {
		return;
		list.add("\t" + prefix + name + typeParams + "(" + paramList + "): " + tsType(returnType) + " {");
		let segs: List<string> = parseSegments(body);
		if (segs.isEmpty()) {
		for (String seg : segs) {
		list.add("\t\t" + seg);
		list.add("\t}");
		private static String tsParams(String javaParams) {
		javaParams = javaParams.trim();
		let out: StringBuilder = new StringBuilder();
		let depth: number = 0;
		let start: number = 0;
		let first: boolean = true;
		for (int i = 0; i <= javaParams.length(); i++) {
		let atEnd: boolean = i == javaParams.length();
		let atComma: boolean = !atEnd && javaParams.charAt(i) == ',' && depth == 0;
		if (atEnd || atComma) {
		let part: string = javaParams.substring(start, i).trim();
		first = appendParam(part, out, first);
		start = i + 1;
		if (javaParams.charAt(i) == '<') {
		else if (javaParams.charAt(i) == '>') {
		return out.toString();
		private static boolean appendParam(String part, StringBuilder out, boolean first) {
		if (part.isEmpty()) {
		return first;
		let last: number = part.lastIndexOf(' ');
		if (last == -1) {
		return first;
		let type: string = part.substring(0, last).trim();
		let name: string = part.substring(last + 1).trim();
		if (!first) {
		out.append(", ");
		out.append(name).append(": ").append(tsType(type));
		return false;
		private static String tsType(String javaType) {
		javaType = javaType.trim();
		if (javaType.endsWith("[]")) {
		let inner: string = javaType.substring(0, javaType.length() - 2);
		return tsType(inner) + "[]";
		let lt: number = javaType.indexOf('<');
		if (lt != -1 && javaType.endsWith(">")) {
		let base: string = javaType.substring(0, lt);
		let args: string = javaType.substring(lt + 1, javaType.length() - 1);
		let converted: List<string> = convertTypes(splitGenericArgs(args));
		converted.replaceAll(JavaFile::sanitizeWildcard);
		let simple: string = base.replace("java.util.function.", "");
		if ("Function".equals(simple) && converted.size() >= 2) {
		return "(arg0: " + converted.get(0) + ") = > " + converted.get(1);
		if ("BiFunction".equals(simple) && converted.size() >= 3) {
		return "(arg0: " + converted.get(0) + ", arg1: " + converted.get(1)
		+ ") = > " + converted.get(2);
		if ("Supplier".equals(simple) && !converted.isEmpty()) {
		return "() = > " + converted.getFirst();
		if ("Consumer".equals(simple) && !converted.isEmpty()) {
		return "(arg0: " + converted.getFirst() + ") = > void";
		if ("BiConsumer".equals(simple) && converted.size() >= 2) {
		return "(arg0: " + converted.get(0) + ", arg1: " + converted.get(1)
		+ ") = > void";
		if ("Predicate".equals(simple) && !converted.isEmpty()) {
		return "(arg0: " + converted.getFirst() + ") = > boolean";
		return base + "<" + String.join(", ", converted) + ">";
		return switch (javaType) {
		private static List<String> splitGenericArgs(String args) {
		let parts: List<string> = new ArrayList<>();
		let depth: number = 0;
		let start: number = 0;
		for (int i = 0; i < args.length(); i++) {
		let ch: string = args.charAt(i);
		if (ch == '<') {
		else if (ch == '>') {
		else if (ch == ',' && depth == 0) {
		parts.add(args.substring(start, i).trim());
		start = i + 1;
		parts.add(args.substring(start).trim());
		return parts;
		private static List<String> convertTypes(List<String> parts) {
		let converted: List<string> = new ArrayList<>();
		for (String part : parts) {
		converted.add(tsType(part));
		return converted;
		private static String parseValue(String value) {
		let out: StringBuilder = new StringBuilder();
		let i: number = 0;
		while (i < value.length()) {
		let ch: string = value.charAt(i);
		if (Character.isWhitespace(ch)) {
		out.append(ch);
		if (ch == '"' || ch == '\'') {
		i = scanStringLiteral(value, i, out);
		if (Character.isDigit(ch)) {
		i = scanNumberLiteral(value, i, out);
		if (Character.isJavaIdentifierStart(ch)) {
		i = scanIdentifier(value, i, out);
		out.append(ch);
		return out.toString().trim();
		private static int scanStringLiteral(String value, int start, StringBuilder out) {
		let quote: string = value.charAt(start);
		let i: number = start + 1;
		let esc: boolean = false;
		while (i < value.length()) {
		let c: string = value.charAt(i);
		if (esc) {
		esc = false;
		} else if (c == '\\') {
		esc = true;
		} else if (c == quote) {
		out.append(value, start, i);
		return i;
		private static int scanNumberLiteral(String value, int start, StringBuilder out) {
		let i: number = start;
		let exp: boolean = false;
		let endDigits: number = start;
		while (i < value.length()) {
		let c: string = value.charAt(i);
		if (c == '_' || Character.isDigit(c) || c == '.' || c == 'x' || c == 'X'
		endDigits = i;
		if ((c == 'e' || c == 'E') && !exp) {
		exp = true;
		endDigits = i;
		if ((c == '+' || c == '-') && exp
		endDigits = i;
		if ("lLfFdD".indexOf(c) != -1) {
		let num: string = value.substring(start, endDigits).replace("_", "");
		out.append(num);
		return i;
		private static int scanIdentifier(String value, int start, StringBuilder out) {
		let i: number = start + 1;
		while (i < value.length() && Character.isJavaIdentifierPart(value.charAt(i))) {
		let id: string = value.substring(start, i);
		if ("instanceof".equals(id)) {
		out.append(" instanceof");
		out.append(id);
		return i;
		private static final Pattern[] SEGMENT_PATTERNS = new Pattern[]{
		Pattern.compile("\\b(class|interface|record)\\b"),
		Pattern.compile("[^ = !<>]=[^=]"),
		private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("[^=!<>]=[^=]");
		private static final Pattern RETURN_PATTERN = Pattern.compile("\\breturn\\b");
		private static final Pattern IF_PATTERN = Pattern.compile("\\bif\\s*\\(");
		private static final Pattern WHILE_PATTERN = Pattern.compile("\\bwhile\\s*\\(");
		private static final Pattern FOR_PATTERN = Pattern.compile("\\bfor\\s*\\(");
		private static List<String> parseSegments(String body) {
		let segments: List<string> = new ArrayList<>();
		for (String line : body.split("\\R")) {
		let stripped: string = line.trim();
		if (stripped.isEmpty()) {
		let seg: string = matchSegment(stripped);
		if (seg != null) {
		segments.add(seg);
		return segments;
		private static String matchSegment(String stripped) {
		for (Pattern pat : SEGMENT_PATTERNS) {
		if (pat.matcher(stripped).find()) {
		return processSegment(stripped);
		return null;
		private static String processSegment(String segment) {
		if (ASSIGNMENT_PATTERN.matcher(segment).find()) {
		let eq: number = segment.indexOf('=');
		if (eq != -1) {
		let before: string = segment.substring(0, eq).trim();
		let after: string = segment.substring(eq + 1).trim();
		let semi: boolean = after.endsWith(";");
		if (semi) {
		after = after.substring(0, after.length() - 1).trim();
		let value: string = parseValue(after);
		let declPat: var = Pattern.compile("^(?:final\\s+)?([\\w.<>\\[\\]]+)\\s+(\\w+)$");
		let declMatch: var = declPat.matcher(before);
		if (declMatch.find()) {
		let type: string = tsType(declMatch.group(1));
		let name: string = declMatch.group(2);
		let isConst: boolean = before.startsWith("final ");
		let kw: string = isConst ? "const" : "let";
		return kw + " " + name + ": " + type + " = " + value + (semi ? ";" : "");
		return before + " = " + value + (semi ? ";" : "");
		if (RETURN_PATTERN.matcher(segment).find()) {
		let rest: string = segment.substring(segment.indexOf("return") + 6).trim();
		let semi: boolean = rest.endsWith(";");
		if (semi) {
		rest = rest.substring(0, rest.length() - 1).trim();
		if (rest.isEmpty()) {
		return segment;
		let value: string = parseValue(rest);
		return "return " + value + (semi ? ";" : "");
		if (IF_PATTERN.matcher(segment).find() || WHILE_PATTERN.matcher(segment).find() || FOR_PATTERN.matcher(segment).find()) {
		let open: number = segment.indexOf('(');
		let close: number = segment.lastIndexOf(')');
		if (open != -1 && close != -1 && close > open) {
		let prefix: string = segment.substring(0, open + 1);
		let inner: string = segment.substring(open + 1, close);
		let suffix: string = segment.substring(close);
		let value: string = parseValue(inner);
		return prefix + value + suffix;
		return segment;
		private static String extractBlock(String source, int start) {
		let level: number = 1;
		let i: number = start;
		while (i < source.length() && level > 0) {
		let ch: string = source.charAt(i);
		if (ch == '{') {
		else if (ch == '}') {
		return source.substring(start, i - 1);
		private static String sanitizeWildcard(String type) {
		type = type.trim();
		if (type.startsWith("? extends ")) {
		return type.substring(10).trim();
		if (type.startsWith("? super ")) {
		return type.substring(8).trim();
		if ("?".equals(type)) {
		return "any";
		return type;
	}
	static addMethod(list: List<string>, staticKw: string, generics: string, returnType: string, name: string, params: string, delim: string, isInterface: boolean, body: string): void {
		let prefix: string = staticKw == null ? "" : "static ";
		let typeParams: string = generics == null ? "" : generics.trim();
		let paramList: string = tsParams(params);
		if (isInterface || ";".equals(delim)) {
		return;
		list.add("\t" + prefix + name + typeParams + "(" + paramList + "): " + tsType(returnType) + " {");
		let segs: List<string> = parseSegments(body);
		if (segs.isEmpty()) {
		for (String seg : segs) {
		list.add("\t\t" + seg);
		list.add("\t}");
	}
	static tsParams(javaParams: string): string {
		javaParams = javaParams.trim();
		let out: StringBuilder = new StringBuilder();
		let depth: number = 0;
		let start: number = 0;
		let first: boolean = true;
		for (int i = 0; i <= javaParams.length(); i++) {
		let atEnd: boolean = i == javaParams.length();
		let atComma: boolean = !atEnd && javaParams.charAt(i) == ',' && depth == 0;
		if (atEnd || atComma) {
		let part: string = javaParams.substring(start, i).trim();
		first = appendParam(part, out, first);
		start = i + 1;
		if (javaParams.charAt(i) == '<') {
		else if (javaParams.charAt(i) == '>') {
		return out.toString();
	}
	StringBuilder(): new;
	static appendParam(part: string, out: StringBuilder, first: boolean): boolean {
		if (part.isEmpty()) {
		return first;
		let last: number = part.lastIndexOf(' ');
		if (last == -1) {
		return first;
		let type: string = part.substring(0, last).trim();
		let name: string = part.substring(last + 1).trim();
		if (!first) {
		out.append(", ");
		out.append(name).append(": ").append(tsType(type));
		return false;
	}
	static tsType(javaType: string): string {
		javaType = javaType.trim();
		if (javaType.endsWith("[]")) {
		let inner: string = javaType.substring(0, javaType.length() - 2);
		return tsType(inner) + "[]";
		let lt: number = javaType.indexOf('<');
		if (lt != -1 && javaType.endsWith(">")) {
		let base: string = javaType.substring(0, lt);
		let args: string = javaType.substring(lt + 1, javaType.length() - 1);
		let converted: List<string> = convertTypes(splitGenericArgs(args));
		converted.replaceAll(JavaFile::sanitizeWildcard);
		let simple: string = base.replace("java.util.function.", "");
		if ("Function".equals(simple) && converted.size() >= 2) {
		return "(arg0: " + converted.get(0) + ") = > " + converted.get(1);
		if ("BiFunction".equals(simple) && converted.size() >= 3) {
		return "(arg0: " + converted.get(0) + ", arg1: " + converted.get(1)
		+ ") = > " + converted.get(2);
		if ("Supplier".equals(simple) && !converted.isEmpty()) {
		return "() = > " + converted.getFirst();
		if ("Consumer".equals(simple) && !converted.isEmpty()) {
		return "(arg0: " + converted.getFirst() + ") = > void";
		if ("BiConsumer".equals(simple) && converted.size() >= 2) {
		return "(arg0: " + converted.get(0) + ", arg1: " + converted.get(1)
		+ ") = > void";
		if ("Predicate".equals(simple) && !converted.isEmpty()) {
		return "(arg0: " + converted.getFirst() + ") = > boolean";
		return base + "<" + String.join(", ", converted) + ">";
		return switch (javaType) {
	}
	switch(): return {
		return 0;
	}
	static splitGenericArgs(args: string): List<string> {
		let parts: List<string> = new ArrayList<>();
		let depth: number = 0;
		let start: number = 0;
		for (int i = 0; i < args.length(); i++) {
		let ch: string = args.charAt(i);
		if (ch == '<') {
		else if (ch == '>') {
		else if (ch == ',' && depth == 0) {
		parts.add(args.substring(start, i).trim());
		start = i + 1;
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
		return converted;
	}
	static parseValue(value: string): string {
		let out: StringBuilder = new StringBuilder();
		let i: number = 0;
		while (i < value.length()) {
		let ch: string = value.charAt(i);
		if (Character.isWhitespace(ch)) {
		out.append(ch);
		if (ch == '"' || ch == '\'') {
		i = scanStringLiteral(value, i, out);
		if (Character.isDigit(ch)) {
		i = scanNumberLiteral(value, i, out);
		if (Character.isJavaIdentifierStart(ch)) {
		i = scanIdentifier(value, i, out);
		out.append(ch);
		return out.toString().trim();
	}
	StringBuilder(): new;
	static scanStringLiteral(value: string, start: number, out: StringBuilder): number {
		let quote: string = value.charAt(start);
		let i: number = start + 1;
		let esc: boolean = false;
		while (i < value.length()) {
		let c: string = value.charAt(i);
		if (esc) {
		esc = false;
		} else if (c == '\\') {
		esc = true;
		} else if (c == quote) {
		out.append(value, start, i);
		return i;
	}
	if('\\': c ==): else {
		esc = true;
	}
	if(quote: c ==): else {
		return 0;
	}
	static scanNumberLiteral(value: string, start: number, out: StringBuilder): number {
		let i: number = start;
		let exp: boolean = false;
		let endDigits: number = start;
		while (i < value.length()) {
		let c: string = value.charAt(i);
		if (c == '_' || Character.isDigit(c) || c == '.' || c == 'x' || c == 'X'
		endDigits = i;
		if ((c == 'e' || c == 'E') && !exp) {
		exp = true;
		endDigits = i;
		if ((c == '+' || c == '-') && exp
		endDigits = i;
		if ("lLfFdD".indexOf(c) != -1) {
		let num: string = value.substring(start, endDigits).replace("_", "");
		out.append(num);
		return i;
	}
	static scanIdentifier(value: string, start: number, out: StringBuilder): number {
		let i: number = start + 1;
		while (i < value.length() && Character.isJavaIdentifierPart(value.charAt(i))) {
		let id: string = value.substring(start, i);
		if ("instanceof".equals(id)) {
		out.append(" instanceof");
		out.append(id);
		return i;
	}
	static parseSegments(body: string): List<string> {
		let segments: List<string> = new ArrayList<>();
		for (String line : body.split("\\R")) {
		let stripped: string = line.trim();
		if (stripped.isEmpty()) {
		let seg: string = matchSegment(stripped);
		if (seg != null) {
		segments.add(seg);
		return segments;
	}
	static matchSegment(stripped: string): string {
		for (Pattern pat : SEGMENT_PATTERNS) {
		if (pat.matcher(stripped).find()) {
		return processSegment(stripped);
		return null;
	}
	processSegment(): return;
	static processSegment(segment: string): string {
		if (ASSIGNMENT_PATTERN.matcher(segment).find()) {
		let eq: number = segment.indexOf('=');
		if (eq != -1) {
		let before: string = segment.substring(0, eq).trim();
		let after: string = segment.substring(eq + 1).trim();
		let semi: boolean = after.endsWith(";");
		if (semi) {
		after = after.substring(0, after.length() - 1).trim();
		let value: string = parseValue(after);
		let declPat: var = Pattern.compile("^(?:final\\s+)?([\\w.<>\\[\\]]+)\\s+(\\w+)$");
		let declMatch: var = declPat.matcher(before);
		if (declMatch.find()) {
		let type: string = tsType(declMatch.group(1));
		let name: string = declMatch.group(2);
		let isConst: boolean = before.startsWith("final ");
		let kw: string = isConst ? "const" : "let";
		return kw + " " + name + ": " + type + " = " + value + (semi ? ";" : "");
		return before + " = " + value + (semi ? ";" : "");
		if (RETURN_PATTERN.matcher(segment).find()) {
		let rest: string = segment.substring(segment.indexOf("return") + 6).trim();
		let semi: boolean = rest.endsWith(";");
		if (semi) {
		rest = rest.substring(0, rest.length() - 1).trim();
		if (rest.isEmpty()) {
		return segment;
		let value: string = parseValue(rest);
		return "return " + value + (semi ? ";" : "");
		if (IF_PATTERN.matcher(segment).find() || WHILE_PATTERN.matcher(segment).find() || FOR_PATTERN.matcher(segment).find()) {
		let open: number = segment.indexOf('(');
		let close: number = segment.lastIndexOf(')');
		if (open != -1 && close != -1 && close > open) {
		let prefix: string = segment.substring(0, open + 1);
		let inner: string = segment.substring(open + 1, close);
		let suffix: string = segment.substring(close);
		let value: string = parseValue(inner);
		return prefix + value + suffix;
		return segment;
	}
	static extractBlock(source: string, start: number): string {
		let level: number = 1;
		let i: number = start;
		while (i < source.length() && level > 0) {
		let ch: string = source.charAt(i);
		if (ch == '{') {
		else if (ch == '}') {
		return source.substring(start, i - 1);
	}
	if('}': ch ==): else {
		return 0;
	}
	static sanitizeWildcard(type: string): string {
		type = type.trim();
		if (type.startsWith("? extends ")) {
		return type.substring(10).trim();
		if (type.startsWith("? super ")) {
		return type.substring(8).trim();
		if ("?".equals(type)) {
		return "any";
		return type;
	}
}

package magma.ast;

import magma.diagnostics.CompileError;
import magma.parser.ParserUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Structs {
	private final Map<String, List<String>> structFields = new HashMap<>();
	// parallel map to hold field types (e.g. "int" or "fn") for C emission
	private final Map<String, List<String>> structFieldTypes = new HashMap<>();

	public void register(String name, List<String> fields) {
		var maybeDup = this.checkDuplicate(name, fields);
		if (maybeDup.isPresent()) return;
		this.structFields.put(name, new ArrayList<>(fields));
		List<String> types = new ArrayList<>();
		var fieldSize = fields.size();
		for (var i = 0; i < fieldSize; i++) {types.add("int");}
		this.structFieldTypes.put(name, types);
	}

	public Optional<CompileError> registerWithTypes(String name, List<String> fields, List<String> types) {
		var dup = this.checkDuplicate(name, fields);
		if (dup.isPresent()) {
			if (this.structFields.containsKey(name)) {
				var existing = this.structFields.get(name);
				var existingTypes = this.structFieldTypes.get(name);
				if (existing.equals(fields) && null != existingTypes && existingTypes.equals(types)) return Optional.empty();
			}
			return dup;
		}
		this.structFields.put(name, new ArrayList<>(fields));
		this.structFieldTypes.put(name, new ArrayList<>(types));
		return Optional.empty();
	}

	private Optional<CompileError> checkDuplicate(String name, List<String> fields) {
		if (this.structFields.containsKey(name)) {
			var existing = this.structFields.get(name);
			if (existing.equals(fields)) {
				return Optional.empty();
			}
			return Optional.of(new CompileError("Duplicate struct: " + name));
		}
		return Optional.empty();
	}

	public List<String> getFieldTypes(String name) {
		return this.structFieldTypes.get(name);
	}

	public String emitCTypeDefs() {
		var out = new StringBuilder();
		for (var e : this.structFields.entrySet()) {
			var sname = e.getKey();
			var fields = e.getValue();
			var types = this.structFieldTypes.getOrDefault(sname, new ArrayList<>());
			out.append("typedef struct { ");
			var fieldSize = fields.size();
			for (var i = 0; i < fieldSize; i++) {
				var f = fields.get(i);
				var t = i < types.size() ? types.get(i) : "int";
				if ("fn".equals(t)) {
					// function pointer returning int with no params
					out.append("int (*").append(f).append(")(); ");
				} else {
					out.append("int ").append(f).append("; ");
				}
			}
			out.append("} ").append(sname).append(";\n");
		}
		return out.toString();
	}

	public StructLiteral parseStructLiteral(String trimmed) {
		var braceIdx = trimmed.indexOf('{');
		if (-1 == braceIdx) return null;
		var maybeName = trimmed.substring(0, braceIdx).trim();
		if (!this.structFields.containsKey(maybeName)) return null;
		var end = ParserUtils.advanceNested(trimmed, braceIdx + 1, '{', '}');
		var inner = -1 == end ? trimmed.substring(braceIdx + 1) : trimmed.substring(braceIdx + 1, end - 1);
		var rawVals = ParserUtils.splitTopLevel(inner, ',', '{', '}');
		List<String> vals = new ArrayList<>();
		for (var v : rawVals) {
			if (null != v && !v.trim().isEmpty()) vals.add(v);
		}
		var fields = this.structFields.get(maybeName);
		return new StructLiteral(maybeName, vals, fields);
	}

	public static String buildStructLiteral(String maybeName, List<String> vals, List<String> fields, boolean forC) {
		if (forC) {
			var lit = new StringBuilder();
			lit.append('(').append(maybeName).append("){");
			for (var i = 0; i < fields.size(); i++) {
				lit.append(Structs.fieldInit(i, fields, vals, true));
			}
			lit.append('}');
			return lit.toString();
		} else {
			var obj = new StringBuilder();
			obj.append('{');
			for (var i = 0; i < fields.size(); i++) {
				obj.append(Structs.fieldInit(i, fields, vals, false));
			}
			obj.append('}');
			return obj.toString();
		}
	}

	private static String fieldInit(int i, List<String> fields, List<String> vals, boolean forC) {
		var t = new StringBuilder();
		if (0 < i) t.append(", ");
		var fn = fields.get(i);
		var val = i < vals.size() ? vals.get(i).trim() : (forC ? "0" : "undefined");
		if (forC) {
			t.append('.').append(fn).append(" = ").append(val);
		} else {
			t.append(fn).append(": ").append(val);
		}
		return t.toString();
	}
}

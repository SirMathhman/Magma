package org.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Helper to manage child env lifecycle (to avoid code duplication)
final class ChildContext {
	final Map<String, String> prevVar;
	final Map<String, FunctionInfo> prevFunc;
	final Set<String> prevStruct;
	final Map<String, String> childVar;
	// childFunc/childStruct are implicitly accessible via ThreadLocals; no fields
	// needed

	private ChildContext(Map<String, String> prevVar,
											 Map<String, FunctionInfo> prevFunc,
											 Set<String> prevStruct,
											 Map<String, String> childVar) {
		this.prevVar = prevVar;
		this.prevFunc = prevFunc;
		this.prevStruct = prevStruct;
		this.childVar = childVar;
	}

	static ChildContext enter() {
		Map<String, String> prevVar = Interpreter.VAR_ENV.get();
		Map<String, FunctionInfo> prevFunc = Interpreter.FUNC_REG.get();
		Set<String> prevStruct = Interpreter.STRUCT_REG.get();

		Map<String, String> parentVar = (prevVar == null) ? new HashMap<>() : prevVar;
		Map<String, FunctionInfo> parentFunc = (prevFunc == null) ? new HashMap<>() : prevFunc;
		Set<String> parentStruct = (prevStruct == null) ? new HashSet<>() : prevStruct;

		Map<String, String> childVar = new HashMap<>(parentVar);
		Map<String, FunctionInfo> childFunc = new HashMap<>(parentFunc);
		Set<String> childStruct = new HashSet<>(parentStruct);

		Interpreter.VAR_ENV.set(childVar);
		Interpreter.FUNC_REG.set(childFunc);
		Interpreter.STRUCT_REG.set(childStruct);

		return new ChildContext(prevVar, prevFunc, prevStruct, childVar);
	}

	void restore(boolean mergeVars) {
		try {
			if (mergeVars && prevVar != null) {
				for (Map.Entry<String, String> e : childVar.entrySet()) {
					if (prevVar.containsKey(e.getKey())) {
						prevVar.put(e.getKey(), e.getValue());
					}
				}
			}
		} finally {
			Interpreter.VAR_ENV.set(prevVar);
			Interpreter.FUNC_REG.set(prevFunc);
			Interpreter.STRUCT_REG.set(prevStruct);
		}
	}
}

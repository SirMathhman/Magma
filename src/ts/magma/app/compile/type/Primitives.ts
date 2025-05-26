import { Node } from "../../../../magma/app/compile/node/Node";
import { Option } from "../../../../magma/api/option/Option";
import { StripRule } from "../../../../magma/app/compile/rule/StripRule";
import { PrimitiveRule } from "../../../../magma/app/compile/type/PrimitiveRule";
export class Primitives {/*public static final Node BOOLEAN = new MapNode("boolean").withString("value", "boolean");*//*
    public static final Node NUMBER = new MapNode("number").withString("value", "number");*//*
    public static final Node STRING = new MapNode("string").withString("value", "string");*//*
    public static final Node UNKNOWN = new MapNode("unknown").withString("value", "unknown");*//*
    public static final Node VAR = new MapNode("var").withString("value", "var");*//*
    public static final Node VOID = new MapNode("void").withString("value", "void");*//*

    public static final Map<String, Node> JavaToVariant = Map.of(
            "boolean", Primitives.BOOLEAN,
            "Boolean", Primitives.BOOLEAN,
            "char", Primitives.STRING,
            "Character", Primitives.STRING,
            "String", Primitives.STRING,
            "int", Primitives.NUMBER,
            "Integer", Primitives.NUMBER,
            "var", Primitives.VAR,
            "void", Primitives.VOID
    );*//*

    public static final Map<String, Node> TypeScriptToVariant = Map.of(
            "boolean", Primitives.BOOLEAN,
            "number", Primitives.NUMBER,
            "string", Primitives.STRING,
            "unknown", Primitives.UNKNOWN,
            "var", Primitives.VAR,
            "void", Primitives.VOID
    );*/
	static createPrimitivesRule(input: string): Option<Node> {
		return new StripRule(new PrimitiveRule()).lex(input)/*unknown*/;
	}
}

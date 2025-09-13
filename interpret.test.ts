import { expect, test, } from "bun:test";
import { interpret } from "./interpret";

test("empty", () => {
	expect(interpret("")).toEqual([0, ""]);
});

test("undefined", () => {
	expect(() => interpret("test")).toThrow();
});
import { expect, test, } from "bun:test";
import { interpret } from "./interpret";

test("empty", () => {
	expect(interpret("")).toEqual([0, ""]);
});

test("undefined", () => {
	expect(() => interpret("test")).toThrow();
});

test("number 5", () => {
	expect(interpret("5")).toEqual([5, ""]);
});

test("number 10", () => {
	expect(interpret("10")).toEqual([10, ""]);
});
import { compile } from "./compile";

describe("The compiler", () => {
	test("returns empty string for empty input", () => {
		expect(compile("")).toBe("");
	});

	test("throws for non-empty input", () => {
		expect(() => compile("hello")).toThrow(Error);
	});
});

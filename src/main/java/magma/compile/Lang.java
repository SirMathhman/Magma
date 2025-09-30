package magma.compile;

import magma.compile.rule.NodeListRule;
import magma.compile.rule.PlaceholderRule;
import magma.compile.rule.Rule;
import magma.compile.rule.StringRule;

import java.util.List;

public class Lang {
	public record Content(String content) {
	}

	public record JavaRoot(List<Content> children) {}

	public record CRoot(List<Content> children) {}

	public static Rule createCRootRule() {
		return new NodeListRule("children", new PlaceholderRule(new StringRule("content")));
	}

	public static Rule createJavaRootRule() {
		return new NodeListRule("children", new StringRule("content"));
	}
}

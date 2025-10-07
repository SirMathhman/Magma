package magma.debug.performance;

import magma.compile.Lang;
import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class TransformerPerformanceTest {
	@Test
	void test() {
		final String s = """
				switch (expression) {
					case Lang.Invalid invalid -> invalid;
					case Lang.Identifier identifier -> identifier;
					case Lang.JFieldAccess fieldAccess ->
							new Lang.CFieldAccess(transformExpression(fieldAccess.child()), fieldAccess.name());
					case Lang.JInvocation jInvocation -> transformInvocation(jInvocation);
					case Lang.JConstruction jConstruction -> handleConstruction(jConstruction);
					case Lang.JAdd add -> new Lang.CAdd(transformExpression(add.left()), transformExpression(add.right()));
					case Lang.JString jString -> new Lang.CString(jString.content().orElse(""));
					case Lang.JEquals jEquals ->
							new Lang.CEquals(transformExpression(jEquals.left()), transformExpression(jEquals.right()));
					case Lang.And and -> new Lang.CAnd(transformExpression(and.left()), transformExpression(and.right()));
					case Lang.CharNode charNode -> charNode;
					case JNodes.JCast cast -> new CNodes.Cast(transformType(cast.type()), transformExpression(cast.child()));
					case Lang.Index index -> new Lang.Invalid("???");
					case Lang.InstanceOf instanceOf -> new Lang.Invalid("???");
					case Lang.JGreaterThan jGreaterThan -> new Lang.Invalid("???");
					case Lang.JGreaterThanEquals jGreaterThanEquals -> new Lang.Invalid("???");
					case Lang.JLessThan jLessThan -> new Lang.Invalid("???");
					case Lang.JLessThanEquals jLessThanEquals -> new Lang.Invalid("???");
					case Lang.JNotEquals jNotEquals -> new Lang.Invalid("???");
					case Lang.JOr jOr -> new Lang.Invalid("???");
					case Lang.JSubtract jSubtract -> new Lang.Invalid("???");
					case Lang.Lambda lambda -> new Lang.Invalid("???");
					case Lang.MethodAccess methodAccess -> new Lang.Invalid("???");
					case Lang.NewArray newArray -> new Lang.Invalid("???");
					case Lang.Not not -> new Lang.Invalid("???");
					case Lang.NumberNode numberNode -> new Lang.Invalid("???");
					case Lang.Quantity quantity -> new Lang.Invalid("???");
					case Lang.SwitchExpr switchExpr -> new Lang.Invalid("???");
				}""";

		Assertions.assertTimeoutPreemptively(Duration.ofSeconds(3), () -> {
			switch (Lang.JExpression(Lang.JMethodSegment()).lex(s)) {
				case Err<Node, CompileError> v -> fail(v.error().display());
				case Ok<Node, CompileError> v -> assertNotNull(v.value());
			}
		});
	}
}

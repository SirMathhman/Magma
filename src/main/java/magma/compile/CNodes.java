package magma.compile;

import magma.list.Joiner;
import magma.list.List;

public class CNodes {
	sealed public interface CType permits Lang.CTemplate, CFunctionPointer, Lang.Identifier, Lang.Invalid, Lang.Pointer {
		String stringify();
	}

	@Tag("functionPointer")
	public record CFunctionPointer(CType returnType, List<CType> paramTypes) implements CType {
		@Override
		public String stringify() {
			return "fn_" + paramTypes.stream().map(CType::stringify).collect(new Joiner("_")) + "_" + returnType.stringify();
		}
	}

	@Tag("cast")
	public record Cast(CType cType, Lang.CExpression cExpression) implements Lang.CExpression {}
}

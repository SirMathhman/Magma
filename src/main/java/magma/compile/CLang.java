package magma.compile;

import java.util.List;
import java.util.stream.Collectors;

public class CLang {
	sealed public interface CType
			permits Lang.CGeneric, CFunctionPointer, Lang.Identifier, Lang.Invalid, Lang.Pointer {
		String stringify();
	}

	@Tag("functionPointer")
	public record CFunctionPointer(CType returnType, List<CType> paramTypes) implements CType {
		@Override
		public String stringify() {
			return "fn_" + paramTypes.stream().map(CType::stringify).collect(Collectors.joining("_")) + "_" +
						 returnType.stringify();
		}
	}
}

package tmp;
import com.example.magma.Compiler;
public class DebugCompile {
  public static void main(String[] args) {
    String source = "extern fn readInt() : I32; " +
      "structure Wrapper {field : I32} let instance = Wrapper {readInt()}; instance.field";
    System.out.println(Compiler.compile(source));
  }
}

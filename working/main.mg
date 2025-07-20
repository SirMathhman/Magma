import stdio;

extern fn printf<Length: U32>(format : &Str, second : [Any; Length]);

fn main() => {
 printf("%s", "Hello World!");
 return 0;
}
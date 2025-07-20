import stdio;

extern fn printf(format : &Str, second : [Any; 5]);

fn main() => {
 printf("%s", "Hello World!");
 return 0;
}
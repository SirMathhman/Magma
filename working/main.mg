import stdio;

extern fn printf<Length: U32>(format : &Str, second : [Any; Length]);

fn main() => {
 for(let mut i = 0; i < 100; i++){
  printf("%d\n", i);
 }

 return 0;
}
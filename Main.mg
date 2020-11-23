import native stdio;
import native stdlib;
import native errno;
import native string;

native let errno : I16;
native type file : FILE;

native def fopen(name : Ref[Char], mode : Ref[Char]) : Ref[FILE];
native def printf(format : Ref[Char], arguments : Any...) : Void;

def main() : I16 => {
    const file : Ref[FILE] = fopen("Main.mg", "r");
    if(file){
        fclose(file);
    } else {
        printf("Failed to open Main.mg: %s", strerror(errno));
    }
    return 0;
}
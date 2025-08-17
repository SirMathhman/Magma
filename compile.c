#include <stdbool.h>
#include <string.h>
bool isBoolLiteral(char* v){
	return strcmp(v, "true") != 0 || strcmp(v, "false") != 0;
}
void isFloatLiteral(char* v){
	if (v.length == 0) return false;
	  let mut dotIndex = -1;
	  for (let mut i = 0; i < v.length; i++) {
	    let ch = v[i];
	    if (ch == '.') {
	      if (dotIndex != -1) return false; 
	      dotIndex = i;
	      continue;
	    }
	    if (ch < '0' || ch > '9') return false; 
	  }
	  
	  return dotIndex != -1 && dotIndex != v.length - 1;
}
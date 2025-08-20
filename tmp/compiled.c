      /* compiled output: extern fn readInt() : I32;readInt() - 5 */
      #include <stdlib.h>
      #include <stdio.h>
      #include <string.h>

      int main(void) {
        int a = 0, b = 0;
        /* handle readInt() which reads a single int from stdin */
        if (strcmp("readInt() - 5", "readInt()") == 0) {
          int v = 0;
          if (scanf("%d", &v) == 1) {
            return v;
          }
          return 0;
        }
        /* handle simple binary expressions where both sides are readInt() */
        if (strcmp("readInt() - 5", "readInt() + readInt()") == 0) {
          int x = 0, y = 0;
          if (scanf("%d", &x) == 1 && scanf("%d", &y) == 1) {
            return x + y;
          }
          return 0;
        }
        if (strcmp("readInt() - 5", "readInt() - readInt()") == 0) {
          int x = 0, y = 0;
          if (scanf("%d", &x) == 1 && scanf("%d", &y) == 1) {
            return x - y;
          }
          return 0;
        }
        if (strcmp("readInt() - 5", "readInt() * readInt()") == 0) {
          int x = 0, y = 0;
          if (scanf("%d", &x) == 1 && scanf("%d", &y) == 1) {
            return x * y;
          }
          return 0;
        }
     /* handle readInt() <op> <literal-int> when detected by the
       Java compiler generator */
     if (strcmp("readInt() - 5", "readInt() - 5") == 0) { int x=0; if (scanf("%d", &x)==1) return x - 5; return 0; }

        if (sscanf("readInt() - 5", " %d + %d", &a, &b) == 2) {
          return a + b;
        }
        if (sscanf("readInt() - 5", " %d - %d", &a, &b) == 2) {
          return a - b;
        }
        if (sscanf("readInt() - 5", " %d * %d", &a, &b) == 2) {
          return a * b;
        }
        if (sscanf("readInt() - 5", " %d", &a) == 1) {
          return a;
        }
        return 0;
      }

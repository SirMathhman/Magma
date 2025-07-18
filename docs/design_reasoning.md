# Design Reasoning

The compiler currently works with a single input file and a single output file.
This decision keeps the interface familiar and mirrors traditional compilers,
which simplifies future command-line integration.  For now, non-empty input is
simply echoed back with a `compiled:` prefix while the empty-file case generates
an empty `main` function in C.  This minimal behavior allows tests to drive the
implementation while leaving room for the real compilation pipeline to evolve.

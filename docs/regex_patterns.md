## Regex patterns

`GenerateDiagram` relies on several regular expressions to extract class names and relationships. These patterns are fairly dense, so a short overview is provided here:

* `^\s*(?:public\s+|protected\s+|private\s+)?(?:static\s+)?(?:final\s+)?(?:sealed\s+)?(?:class|interface)\s+(\w+)`
  - Matches a class or interface declaration at the start of a line. Optional modifiers such as `public` or `final` are allowed. The captured group is the declared name.
* `(?:class|interface)\s+(\w+)\s+extends\s+([\w\s,<>]+)`
  - Captures inheritance relationships. Group 1 is the child class or interface and group 2 contains the comma-separated parents.
* `class\s+(\w+)(?:\s+extends\s+\w+)?\s+implements\s+([\w\s,<>]+)`
  - Captures implemented interfaces. Group 1 holds the class name and group 2 lists the interfaces.

Before applying these patterns the program strips generic parameters such as `<T>` so that the regexes operate purely on class names.

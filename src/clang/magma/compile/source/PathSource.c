#include "PathSource.h"
struct List__String computeNamespace(){Path_ relative = sourceDirectory.relativize(source);
        Path_ parent = relative.getParent();return parent.stream().collect(ListCollector_());
}
struct String computeName(){String nameWithExt = source().getFileName().asString();return nameWithExt.substring(0, nameWithExt.lastIndexOf("."));
}
struct Result_String_IOError readString(){return source.readString();
}


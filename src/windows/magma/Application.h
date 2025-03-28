#ifndef magma_Application
#define magma_Application
#include "../jv/api/collect/JavaLists.h"
#include "../jv/api/error/ThrowableError.h"
#include "../jv/api/io/JavaFiles.h"
#include "../jv/api/result/JavaResults.h"
#include "../jv/app/compile/PathSource.h"
#include "../magma/api/collect/List_.h"
#include "../magma/api/option/Option.h"
#include "../magma/api/option/Some.h"
#include "../magma/api/result/Err.h"
#include "../magma/api/result/Ok.h"
#include "../magma/api/result/Result.h"
#include "../magma/api/result/Tuple.h"
#include "../magma/app/ApplicationError.h"
#include "../magma/app/compile/ImmutableState.h"
#include "../magma/app/compile/Source.h"
#include "../magma/app/compile/State.h"
#include "../java/nio/file/Files.h"
#include "../java/nio/file/Path.h"
#include "../java/nio/file/Paths.h"
#include "../java/util/ArrayList.h"
#include "../java/util/List.h"
#include "../java/util/Optional.h"
#include "../java/util/Set.h"
#include "../java/util/stream/Collectors.h"
struct Application {
};
#endif

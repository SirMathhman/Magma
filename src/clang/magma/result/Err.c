#include "Err.h"
// expand Result_T_X = Result<struct T, struct X>
// expand Option_T = Option<struct T>
// expand Option_X = Option<struct X>
// expand Result_R_X = Result<struct R, struct X>
// expand Result_R_X = Result<struct R, struct X>
// expand Result_R_X = Result<struct R, struct X>
// expand Result_T_R = Result<struct T, struct R>
// expand Result_Tuple_T_R_X = Result<struct Tuple_T_R, struct X>
// expand Tuple_T_R = Tuple<struct T, struct R>
// expand Supplier_Result_R_X = Supplier<struct Result_R_X>
// expand Result_R_X = Result<struct R, struct X>
struct Option_T findValue(){return new None<>();}struct Option_X findError(){return new Some<>(error);}struct Result_R_X mapValue(struct R(*mapper)(struct T)){return new Err<>(error);}struct Result_R_X flatMapValue(struct Result_R_X(*mapper)(struct T)){return new Err<>(error);}struct Result_T_R mapErr(struct R(*mapper)(struct X)){return new Err<>(mapper.apply(error));}struct R match(struct R(*whenOk)(struct T), struct R(*whenErr)(struct X)){return whenErr.apply(error);}struct Result_Tuple_T_R_X and(struct Supplier_Result_R_X other){return new Err<>(error);}
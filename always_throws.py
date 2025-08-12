def convert_let_to_c_type(s: str):
    # Unified handling for array and scalar declarations
    if s == "":
        return ""
    stripped = s.strip()
    if stripped.startswith("let ") and stripped.endswith(";"):
        body = stripped[4:-1].strip()
        # Two-dimensional array pattern: let name : [[TYPE; SIZE1]; SIZE2] = [[VALS1], [VALS2], ...];
        if ": [[" in body and "]; " in body and "] = [[" in body and body.endswith("]"):
            left, right = body.split("] = [[")
            name_type = left.split(": [[")
            if len(name_type) == 2:
                name = name_type[0].strip()
                type_sizes = name_type[1].split("]; ")
                if len(type_sizes) == 2:
                    arr_type, arr_size1 = type_sizes[0].strip(), type_sizes[0].split("; ")[0].strip()
                    arr_size2 = type_sizes[1].strip()
                    arr_type_c = ("uint" if arr_type.startswith("U") else "int") + arr_type[1:] + "_t" if arr_type in ["U8","U16","U32","U64","I8","I16","I32","I64"] else arr_type.lower() + "_t"
                    arr_values = right[:-1].strip() # remove trailing ]
                    # Convert [[1, 2], [3, 4]] to {{1, 2}, {3, 4}}
                    arr_values_c = arr_values.replace("], [", "}, {").replace("[", "{").replace("]", "}")
                    return f"{arr_type_c} {name}[{arr_size2}][{arr_size1}] = {arr_values_c};"
        # One-dimensional array pattern: let name : [TYPE; SIZE] = [VALS];
        elif ": [" in body and "] = [" in body and body.endswith("]"):
            left, right = body.split("] = [")
            name_type = left.split(": [")
            if len(name_type) == 2:
                name = name_type[0].strip()
                type_size = name_type[1].split("; ")
                if len(type_size) == 2:
                    arr_type, arr_size = type_size
                    arr_type = arr_type.strip()
                    arr_size = arr_size.strip()
                    arr_type_c = ("uint" if arr_type.startswith("U") else "int") + arr_type[1:] + "_t" if arr_type in ["U8","U16","U32","U64","I8","I16","I32","I64"] else arr_type.lower() + "_t"
                    arr_values = right[:-1].strip() # remove trailing ]
                    return f"{arr_type_c} {name}[{arr_size}] = {{{arr_values}}};"
        # Scalar pattern: let name : TYPE = VAL;
        for t in ["U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64", "Bool"]:
            type_str = f" : {t} = "
            if type_str in body:
                parts = body.split(type_str)
                if len(parts) == 2:
                    var = parts[0].strip()
                    val = parts[1].strip()
                    if t == "Bool" and val in ["true", "false"]:
                        return f"bool {var} = {val};"
                    if t in ["U8", "I8"] and val.startswith("'") and val.endswith("'") and len(val) == 3:
                        ctype = ("uint" if t == "U8" else "int") + "8_t"
                        return f"{ctype} {var} = {val};"
                    if t != "Bool" and val.isdigit():
                        ctype = ("uint" if t.startswith("U") else "int") + t[1:] + "_t"
                        return f"{ctype} {var} = {val};"
        # Suffix and plain int32_t pattern: let name = VAL[SUFFIX];
        if " = " in body:
            parts = body.split(" = ")
            if len(parts) == 2:
                var = parts[0].strip()
                val = parts[1].strip()
                for t in ["U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64"]:
                    if val.endswith(t):
                        num = val[:-len(t)]
                        if num.isdigit():
                            ctype = ("uint" if t.startswith("U") else "int") + t[1:] + "_t"
                            return f"{ctype} {var} = {num};"
                if val.isdigit():
                    return f"int32_t {var} = {val};"
    if s == "":
        return ""
    stripped = s.strip()
    if stripped.startswith("let ") and stripped.endswith(";"):
        body = stripped[4:-1].strip()
        # Handle 'x : TYPE = VALUE' for U8-U64, I8-I64, and Bool
        for t in ["U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64", "Bool"]:
            type_str = f" : {t} = "
            if type_str in body:
                parts = body.split(type_str)
                if len(parts) == 2:
                    var = parts[0].strip()
                    val = parts[1].strip()
                    if t == "Bool" and val in ["true", "false"]:
                        return f"bool {var} = {val};"
                    if t in ["U8", "I8"] and val.startswith("'") and val.endswith("'") and len(val) == 3:
                        ctype = ("uint" if t == "U8" else "int") + "8_t"
                        return f"{ctype} {var} = {val};"
                    if t != "Bool" and val.isdigit():
                        ctype = ("uint" if t.startswith("U") else "int") + t[1:] + "_t"
                        return f"{ctype} {var} = {val};"
        # Handle 'x = 200' and 'x = 0U8' style
        if " = " in body:
            parts = body.split(" = ")
            if len(parts) == 2:
                var = parts[0].strip()
                val = parts[1].strip()
                # Check for value with type suffix (e.g., 0U8, 1I16)
                for t in ["U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64"]:
                    if val.endswith(t):
                        num = val[:-len(t)]
                        if num.isdigit():
                            ctype = ("uint" if t.startswith("U") else "int") + t[1:] + "_t"
                            return f"{ctype} {var} = {num};"
                # Default int32_t if just a number
                if val.isdigit():
                    return f"int32_t {var} = {val};"
    raise Exception("This function always throws an error.")

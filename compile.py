def compile(input_string: str) -> str:
    # Function with one parameter: 'fn accept(value : I32) : Void => {}' becomes 'void accept(int32_t value){}'
    if (
        input_string.startswith("fn ")
        and "(" in input_string
        and ")" in input_string
        and " : Void =>" in input_string
        and input_string.endswith("{}")
    ):
        name_start = 3
        paren_start = input_string.find("(", name_start)
        paren_end = input_string.find(")", paren_start)
        name = input_string[name_start:paren_start].strip()
        param_str = input_string[paren_start + 1 : paren_end].strip()
        if param_str:
            param_parts = param_str.split(":")
            if len(param_parts) == 2:
                param_name = param_parts[0].strip()
                param_type = param_parts[1].strip()
                type_map = {"I32": "int32_t"}
                if (
                    param_type in type_map
                    and param_name
                    and all(c.isalnum() or c == "_" for c in param_name)
                ):
                    return f"void {name}({type_map[param_type]} {param_name}){{}}"
    # Simple function definition: 'fn empty() : Void => {}' becomes 'void empty(){}'
    if (
        input_string.startswith("fn ")
        and "() : Void =>" in input_string
        and input_string.endswith("{}")
    ):
        name_start = 3
        name_end = input_string.find("()", name_start)
        if name_end != -1:
            name = input_string[name_start:name_end].strip()
            if name and all(c.isalnum() or c == "_" for c in name):
                return f"void {name}(){{}}"
    # Support multiple let statements outside of braces: 'let x = 0; let y = x;' (untyped only)
    if (
        input_string.startswith("let ")
        and ";" in input_string
        and ":" not in input_string
    ):
        stmts = [stmt.strip() for stmt in input_string.split(";") if stmt.strip()]
        result = []
        for stmt in stmts:
            if stmt.startswith("let ") and "=" in stmt:
                left, right = stmt[len("let ") :].split("=", 1)
                var_name = left.strip()
                value = right.strip()
                if var_name and all(c.isalnum() or c == "_" for c in var_name):
                    result.append(f"int {var_name} = {value};")
                else:
                    return ""
            else:
                return ""
        return " ".join(result)
    # Support for an arbitrary number of let statements inside braces: '{let x = 1; let y = 2;}'
    if input_string.startswith("{") and input_string.endswith("}"):
        inner = input_string[1:-1].strip()
        if inner:
            stmts = [stmt.strip() for stmt in inner.split(";") if stmt.strip()]
            result = []
            for stmt in stmts:
                if stmt.startswith("let ") and "=" in stmt:
                    left, right = stmt[len("let ") :].split("=", 1)
                    var_name = left.strip()
                    value = right.strip()
                    if var_name and all(c.isalnum() or c == "_" for c in var_name):
                        result.append(f"int {var_name} = {value};")
                    else:
                        return ""
                else:
                    return ""
            return "{" + " ".join(result) + "}"
    # Empty braces support: '{}' becomes '{}'
    if input_string.strip() == "{}":
        return "{}"
    # Mutability: 'let mut x = 100; x = 200;' is valid, 'let x = 100; x = 200;' is invalid
    if input_string.startswith("let mut "):
        # Only support 'let mut x = <value>; x = <value>;' for now
        parts = input_string.split(";")
        if len(parts) == 3 and parts[2].strip() == "":
            decl = parts[0].strip()
            assign = parts[1].strip()
            # Parse declaration: 'let mut x = 100'
            if decl.startswith("let mut ") and "=" in decl:
                left, right = decl[len("let mut ") :].split("=", 1)
                var_name = left.strip()
                value1 = right.strip()
                # Parse assignment: 'x = 200'
                if assign.startswith(f"{var_name} = "):
                    value2 = assign[len(f"{var_name} = ") :].strip()
                    if var_name and all(c.isalnum() or c == "_" for c in var_name):
                        return f"int {var_name} = {value1}; {var_name} = {value2};"
    elif input_string.startswith("let "):
        # Only support 'let x = <value>; x = <value>;' for now
        parts = input_string.split(";")
        if len(parts) == 3 and parts[2].strip() == "":
            decl = parts[0].strip()
            assign = parts[1].strip()
            # Parse declaration: 'let x = 100'
            if decl.startswith("let ") and "=" in decl:
                left, right = decl[len("let ") :].split("=", 1)
                var_name = left.strip()
                value1 = right.strip()
                # Parse assignment: 'x = 200'
                if assign.startswith(f"{var_name} = "):
                    value2 = assign[len(f"{var_name} = ") :].strip()
                    # If not mut, mutation is invalid
                    return ""
    """Returns an empty string unless input is 'let x : I32 = 0;', then returns 'int32_t x = 0;'"""
    # Simple C struct support: 'struct Name {}' becomes 'struct Name {};'
    if input_string.startswith("struct ") and input_string.endswith("{}"):
        name = input_string[len("struct ") : -2].strip()
        if (
            name
            and (name[0].isalpha() or name[0] == "_")
            and all(c.isalnum() or c == "_" for c in name)
        ):
            return f"struct {name} {{}};"
    prefix = "let "
    if input_string.startswith(prefix):
        valid_types = {
            "U8": "uint8_t",
            "U16": "uint16_t",
            "U32": "uint32_t",
            "U64": "uint64_t",
            "I8": "int8_t",
            "I16": "int16_t",
            "I32": "int32_t",
            "I64": "int64_t",
        }
        # Numeric assignment
        if input_string.endswith(" = 0;"):
            middle = input_string[len(prefix) : -len(" = 0;")]
            parts = middle.split(" : ")
            if len(parts) == 2:
                var_name, type_name = parts
                if (
                    var_name
                    and all(c.isalnum() or c == "_" for c in var_name)
                    and type_name in valid_types
                ):
                    return f"{valid_types[type_name]} {var_name} = 0;"
        # Boolean assignment
        elif input_string.endswith(" = true;") or input_string.endswith(" = false;"):
            value = "true" if input_string.endswith(" = true;") else "false"
            middle = input_string[len(prefix) : -len(f" = {value};")]
            parts = middle.split(" : ")
            if len(parts) == 2:
                var_name, type_name = parts
                if (
                    var_name
                    and all(c.isalnum() or c == "_" for c in var_name)
                    and type_name == "Bool"
                ):
                    return f"bool {var_name} = {value};"
        # Numeric comparison operators
        else:
            for op in ["==", "!=", "<=", ">=", "<", ">"]:
                if f" {op} " in input_string and input_string.endswith(";"):
                    # Example: 'let x : I32 == 42;'
                    middle = input_string[
                        len(prefix) : -1
                    ]  # remove 'let ' and trailing ';'
                    parts = middle.split(" : ")
                    if len(parts) == 2:
                        var_name, rest = parts
                        for t in ["I32", "U32"]:
                            if rest.startswith(t + f" {op} "):
                                value = rest[len(t + f" {op} ") :]
                                c_type = "int32_t" if t == "I32" else "uint32_t"
                                if var_name and all(
                                    c.isalnum() or c == "_" for c in var_name
                                ):
                                    return f"{c_type} {var_name} = {var_name} {op} {value};"
    # If/else statement support: must be 'if (<condition>) { <body> }' or 'if (<condition>) { <body> } else { <body> }'
    if input_string.startswith("if (") and ") {" in input_string:
        # Check for else with required braces
        else_token = "} else {"
        else_index = input_string.find(else_token, 0)
        if else_index != -1 and input_string.endswith("}"):
            # Parse if and else bodies
            cond_start = 4
            cond_end = input_string.find(") {", cond_start)
            if cond_end != -1:
                condition = input_string[cond_start:cond_end]
                if_body_start = cond_end + 3
                if_body_end = else_index
                if_body = input_string[if_body_start:if_body_end].strip()
                else_body_start = else_index + len(else_token)
                else_body_end = len(input_string) - 1
                else_body = input_string[else_body_start:else_body_end].strip()
                return f"if ({condition}) {{ {if_body} }} else {{ {else_body} }}"
        # Only allow if statement if no else
        elif input_string.endswith("}") and "else" not in input_string:
            cond_start = 4
            cond_end = input_string.find(") {", cond_start)
            if cond_end != -1:
                condition = input_string[cond_start:cond_end]
                body_start = cond_end + 3
                body_end = len(input_string) - 1
                body = input_string[body_start:body_end].strip()
                return f"if ({condition}) {{ {body} }}"
    # While statement support: must be 'while (<condition>) { <body> }'
    if (
        input_string.startswith("while (")
        and ") {" in input_string
        and input_string.endswith("}")
    ):
        cond_start = 7
        cond_end = input_string.find(") {", cond_start)
        if cond_end != -1:
            condition = input_string[cond_start:cond_end]
            body_start = cond_end + 3
            body_end = len(input_string) - 1
            body = input_string[body_start:body_end].strip()
            return f"while ({condition}) {{ {body} }}"
    return ""

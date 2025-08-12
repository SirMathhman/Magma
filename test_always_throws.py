import unittest
from always_throws import convert_let_to_c_type

class TestConvertLetToCType(unittest.TestCase):
    def test_u8_char_literal(self):
        self.assertEqual(convert_let_to_c_type("let x : U8 = 'a';"), "uint8_t x = 'a';")
    def test_bool_true(self):
        self.assertEqual(convert_let_to_c_type("let value : Bool = true;"), "bool value = true;")
    def test_bool_false(self):
        self.assertEqual(convert_let_to_c_type("let value : Bool = false;"), "bool value = false;")
    def test_conversion_x_u16_suffix(self):
        self.assertEqual(always_throws("let x = 1U16;"), "uint16_t x = 1;")
    def test_conversion_x_u32_suffix(self):
        self.assertEqual(always_throws("let x = 2U32;"), "uint32_t x = 2;")
    def test_conversion_x_u64_suffix(self):
        self.assertEqual(always_throws("let x = 3U64;"), "uint64_t x = 3;")
    def test_conversion_x_i8_suffix(self):
        self.assertEqual(always_throws("let x = 4I8;"), "int8_t x = 4;")
    def test_u16_suffix(self):
        self.assertEqual(convert_let_to_c_type("let x = 1U16;"), "uint16_t x = 1;")
    def test_u32_suffix(self):
        self.assertEqual(convert_let_to_c_type("let x = 2U32;"), "uint32_t x = 2;")
    def test_u64_suffix(self):
        self.assertEqual(convert_let_to_c_type("let x = 3U64;"), "uint64_t x = 3;")
    def test_i8_suffix(self):
        self.assertEqual(convert_let_to_c_type("let x = 4I8;"), "int8_t x = 4;")
    def test_i16_suffix(self):
        self.assertEqual(convert_let_to_c_type("let x = 5I16;"), "int16_t x = 5;")
    def test_i32_suffix(self):
        self.assertEqual(convert_let_to_c_type("let x = 6I32;"), "int32_t x = 6;")
    def test_i64_suffix(self):
        self.assertEqual(convert_let_to_c_type("let x = 7I64;"), "int64_t x = 7;")
    def test_u8_suffix(self):
        self.assertEqual(convert_let_to_c_type("let x = 0U8;"), "uint8_t x = 0;")
    def test_u16_declaration(self):
        self.assertEqual(convert_let_to_c_type("let x : U16 = 1;"), "uint16_t x = 1;")
    def test_u32_declaration(self):
        self.assertEqual(convert_let_to_c_type("let x : U32 = 2;"), "uint32_t x = 2;")
    def test_u64_declaration(self):
        self.assertEqual(convert_let_to_c_type("let x : U64 = 3;"), "uint64_t x = 3;")
    def test_i8_declaration(self):
        self.assertEqual(convert_let_to_c_type("let x : I8 = 4;"), "int8_t x = 4;")
    def test_i16_declaration(self):
        self.assertEqual(convert_let_to_c_type("let x : I16 = 5;"), "int16_t x = 5;")
    def test_i32_declaration(self):
        self.assertEqual(convert_let_to_c_type("let x : I32 = 6;"), "int32_t x = 6;")
    def test_i64_declaration(self):
        self.assertEqual(convert_let_to_c_type("let x : I64 = 7;"), "int64_t x = 7;")
    def test_u8_declaration(self):
        self.assertEqual(convert_let_to_c_type("let x : U8 = 0;"), "uint8_t x = 0;")
    def test_plain_int32(self):
        self.assertEqual(convert_let_to_c_type("let x = 200;"), "int32_t x = 200;")
    def test_i32_declaration_200(self):
        self.assertEqual(convert_let_to_c_type("let x : I32 = 200;"), "int32_t x = 200;")
    def test_i32_declaration_y_100(self):
        self.assertEqual(convert_let_to_c_type("let y : I32 = 100;"), "int32_t y = 100;")
    def test_i32_declaration_x_100(self):
        self.assertEqual(convert_let_to_c_type("let x : I32 = 100;"), "int32_t x = 100;")
    def test_error_on_invalid(self):
        with self.assertRaises(Exception) as context:
            convert_let_to_c_type("not empty")
        self.assertEqual(str(context.exception), "This function always throws an error.")
    def test_empty_string(self):
        self.assertEqual(convert_let_to_c_type(""), "")

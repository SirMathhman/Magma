import unittest
from always_throws import always_throws

class TestAlwaysThrows(unittest.TestCase):
    def test_conversion_x_u16_suffix(self):
        self.assertEqual(always_throws("let x = 1U16;"), "uint16_t x = 1;")
    def test_conversion_x_u32_suffix(self):
        self.assertEqual(always_throws("let x = 2U32;"), "uint32_t x = 2;")
    def test_conversion_x_u64_suffix(self):
        self.assertEqual(always_throws("let x = 3U64;"), "uint64_t x = 3;")
    def test_conversion_x_i8_suffix(self):
        self.assertEqual(always_throws("let x = 4I8;"), "int8_t x = 4;")
    def test_conversion_x_i16_suffix(self):
        self.assertEqual(always_throws("let x = 5I16;"), "int16_t x = 5;")
    def test_conversion_x_i32_suffix(self):
        self.assertEqual(always_throws("let x = 6I32;"), "int32_t x = 6;")
    def test_conversion_x_i64_suffix(self):
        self.assertEqual(always_throws("let x = 7I64;"), "int64_t x = 7;")
    def test_conversion_x_u8_suffix(self):
        self.assertEqual(always_throws("let x = 0U8;"), "uint8_t x = 0;")
    def test_conversion_x_u16(self):
        self.assertEqual(always_throws("let x : U16 = 1;"), "uint16_t x = 1;")
    def test_conversion_x_u32(self):
        self.assertEqual(always_throws("let x : U32 = 2;"), "uint32_t x = 2;")
    def test_conversion_x_u64(self):
        self.assertEqual(always_throws("let x : U64 = 3;"), "uint64_t x = 3;")
    def test_conversion_x_i8(self):
        self.assertEqual(always_throws("let x : I8 = 4;"), "int8_t x = 4;")
    def test_conversion_x_i16(self):
        self.assertEqual(always_throws("let x : I16 = 5;"), "int16_t x = 5;")
    def test_conversion_x_i32(self):
        self.assertEqual(always_throws("let x : I32 = 6;"), "int32_t x = 6;")
    def test_conversion_x_i64(self):
        self.assertEqual(always_throws("let x : I64 = 7;"), "int64_t x = 7;")
    def test_conversion_x_u8(self):
        self.assertEqual(always_throws("let x : U8 = 0;"), "uint8_t x = 0;")
    def test_conversion_x_plain(self):
        self.assertEqual(always_throws("let x = 200;"), "int32_t x = 200;")
    def test_conversion_x_i32(self):
        self.assertEqual(always_throws("let x : I32 = 200;"), "int32_t x = 200;")
    def test_conversion_y(self):
        self.assertEqual(always_throws("let y : I32 = 100;"), "int32_t y = 100;")
    def test_conversion(self):
        self.assertEqual(always_throws("let x : I32 = 100;"), "int32_t x = 100;")
    def test_always_throws_error(self):
        with self.assertRaises(Exception) as context:
            always_throws("not empty")
        self.assertEqual(str(context.exception), "This function always throws an error.")

    def test_always_throws_empty(self):
        self.assertEqual(always_throws(""), "")

if __name__ == "__main__":
    unittest.main()

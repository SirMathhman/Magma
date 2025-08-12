import unittest
from always_throws import always_throws

class TestAlwaysThrows(unittest.TestCase):
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

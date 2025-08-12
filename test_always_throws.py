import unittest
from always_throws import always_throws

class TestAlwaysThrows(unittest.TestCase):
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

import unittest
from src.always_throw import always_throws


class TestAlwaysThrow(unittest.TestCase):
    def test_empty_input_returns_string(self):
        result = always_throws("")
        self.assertIsInstance(result, str)
        self.assertEqual(result, "empty")

    def test_non_empty_input_raises(self):
        with self.assertRaises(RuntimeError) as cm:
            always_throws("not empty")
        self.assertEqual(str(cm.exception), "Input must be empty")


if __name__ == "__main__":
    unittest.main()

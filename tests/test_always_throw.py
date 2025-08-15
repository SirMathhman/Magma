import unittest
from src.always_throw import always_throw


class TestAlwaysThrow(unittest.TestCase):
    def test_always_throws_runtime_error(self):
        with self.assertRaises(RuntimeError) as cm:
            always_throw()
        self.assertEqual(str(cm.exception), "This function always throws")


if __name__ == "__main__":
    unittest.main()

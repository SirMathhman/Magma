import unittest
from always_throws import always_throws

class TestAlwaysThrows(unittest.TestCase):
    def test_always_throws(self):
        with self.assertRaises(Exception) as context:
            always_throws()
        self.assertEqual(str(context.exception), "This function always throws an error.")

if __name__ == "__main__":
    unittest.main()

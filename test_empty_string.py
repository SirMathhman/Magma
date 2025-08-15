import unittest
import sys
import os

from empty_string import always_empty

class TestAlwaysEmpty(unittest.TestCase):
    def test_returns_empty_string(self):
        s = always_empty()
        self.assertEqual(s, "")

if __name__ == "__main__":
    unittest.main()

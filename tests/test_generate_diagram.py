import subprocess
import tempfile
from pathlib import Path
import sys
import unittest

SCRIPT = Path(__file__).resolve().parents[1] / 'generate_diagram.py'

class TestGenerateDiagram(unittest.TestCase):
    def test_diagram_created_with_expected_content(self):
        with tempfile.TemporaryDirectory() as tmpdir:
            subprocess.run([sys.executable, str(SCRIPT)], check=True, cwd=tmpdir)
            diagram_path = Path(tmpdir) / 'diagram.puml'
            self.assertTrue(diagram_path.is_file(), 'diagram.puml was not created')
            with open(diagram_path) as f:
                content = f.read()
        expected = "@startuml\nBob -> Alice : hello\n@enduml\n"
        self.assertEqual(content, expected)

if __name__ == '__main__':
    unittest.main()

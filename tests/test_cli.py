import shutil
import subprocess
import sys
from pathlib import Path


def test_init_can_run_directly(tmp_path):
    repo_root = Path(__file__).resolve().parents[1]
    # copy source package and example working directory
    shutil.copytree(repo_root / "src", tmp_path / "src")
    shutil.copytree(repo_root / "working", tmp_path / "working")
    script = tmp_path / "src" / "magma" / "__init__.py"
    subprocess.check_call([sys.executable, str(script)], cwd=tmp_path)
    assert (tmp_path / "working" / "main.c").exists()

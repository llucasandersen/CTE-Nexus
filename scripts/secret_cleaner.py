import os
import re
from pathlib import Path

SENSITIVE_PATTERNS = [
    re.compile(r'jdbc:\\w+://[^\s"]+'),
    re.compile(r'(?i)password\s*=\s*"?[\w]*"?'),
    re.compile(r'AIza[0-9A-Za-z-_]{35}'),
]


def scan_file(path: Path):
    try:
        text = path.read_text(errors='ignore')
    except Exception:
        return []
    findings = []
    for pat in SENSITIVE_PATTERNS:
        for match in pat.findall(text):
            findings.append((path, match))
    return findings


def main(root='.'):  # default to current directory
    root_path = Path(root)
    findings = []
    for p in root_path.rglob('*'):
        if p.is_file():
            findings.extend(scan_file(p))

    if not findings:
        print('No potential secrets found.')
        return

    print('Potential secrets:')
    for file, match in findings:
        print(f'{file}: {match}')

if __name__ == '__main__':
    main()

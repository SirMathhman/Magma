#!/usr/bin/env python3
"""Simple program to create a UML diagram file."""

def main():
    content = """@startuml
Bob -> Alice : hello
@enduml
"""
    with open('diagram.puml', 'w') as f:
        f.write(content)

if __name__ == '__main__':
    main()

#!/usr/bin/env python3
"""
start.py — starts a local HTTP server for the SIC skill browser.

Usage:
    python start.py              # serve on port 8080
    python start.py --port 9000
"""

import argparse
import http.server
import os

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
DEFAULT_PORT = 8080


def serve(port: int = DEFAULT_PORT) -> None:
    os.chdir(SCRIPT_DIR)
    handler = http.server.SimpleHTTPRequestHandler
    print("-----------------------------------------------")
    print(" Second-in-Command Skill Browser")
    print(f" http://localhost:{port}")
    print(" Press Ctrl+C to stop")
    print("-----------------------------------------------")
    with http.server.HTTPServer(("", port), handler) as httpd:
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\nServer stopped.")


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Serve the SIC skill browser over HTTP."
    )
    parser.add_argument(
        "--port", type=int, default=DEFAULT_PORT,
        help=f"HTTP server port (default: {DEFAULT_PORT})"
    )
    args = parser.parse_args()
    serve(args.port)


if __name__ == "__main__":
    main()


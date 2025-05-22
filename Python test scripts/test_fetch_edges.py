#!/usr/bin/env python3
"""
query_edges.py – authenticate against an OpenEMS backend WebSocket and list edges
-------------------------------------------------------------------------------
requirements:
    pip install websocket-client
usage:
    python query_edges.py --url ws://localhost:8082 \
                          --user admin --password admin \
                          --limit 20 --page 0
"""

import json
import uuid
import argparse
import websocket  # pip install websocket-client


def jsonrpc_request(method: str, params: dict | None = None, req_id: str | None = None):
    """Return a JSON-RPC 2.0 request object."""
    return {
        "jsonrpc": "2.0",
        "id": req_id or str(uuid.uuid4()),
        "method": method,
        "params": params or {},
    }


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--url",      default="ws://localhost:8082", help="Backend WebSocket URL")
    ap.add_argument("--user",     default="admin",               help="Username")
    ap.add_argument("--password", default="admin",               help="Password")
    ap.add_argument("--limit",    type=int, default=20,          help="Rows per page")
    ap.add_argument("--page",     type=int, default=0,           help="Page number (0-based)")
    args = ap.parse_args()

    # ───────── 1) open websocket ─────────
    ws = websocket.create_connection(args.url)
    print(f"Connected to {args.url}")

    # ───────── 2) authenticate ─────────
    auth_id = str(uuid.uuid4())
    auth_req = jsonrpc_request(
        "authenticateWithPassword",
        {"username": args.user, "password": args.password},
        auth_id,
    )
    ws.send(json.dumps(auth_req))
    print("» Sent authenticateWithPassword")

    while True:                                  # wait for auth reply
        msg = json.loads(ws.recv())
        if msg.get("id") == auth_id:
            if "result" in msg:
                print("✔️  Authenticated")
                break
            raise SystemExit(f"Authentication failed: {msg.get('error')}")

    # ───────── 3) getEdges ─────────
    edges_id = str(uuid.uuid4())
    get_edges_req = jsonrpc_request(
        "getEdges",
        {"limit": args.limit, "page": args.page, "searchParams": {}},
        edges_id,
    )
    ws.send(json.dumps(get_edges_req))
    print("» Sent getEdges request")

    # listen for response
    while True:
        msg = json.loads(ws.recv())
        if msg.get("id") == edges_id:
            if "result" in msg:
                result = msg["result"]

                # handle any of the common shapes
                if isinstance(result, list):
                    edges = result
                elif "results" in result:
                    edges = result["results"]
                elif "edges" in result:
                    edges = result["edges"]
                else:
                    # fallback: first list-like value inside the dict
                    edges = next(
                        (v for v in result.values() if isinstance(v, list)),
                        []
                    )

                print(f"\n↘️  Received {len(edges)} edge(s):")
                for e in edges:
                    print(f"  • {e.get('name') or e.get('id') or e}")
            else:
                print("⚠️  getEdges returned error:", msg.get("error"))
            break

    ws.close()
    print("Connection closed")


if __name__ == "__main__":
    main()




#!/usr/bin/env python3
"""
Query current channel values from OpenEMS via JSON-RPC edgeRpc.

✱ Requires:   pip install websocket-client
✱ Usage:      python query_edges_channels.py
"""
import json
import uuid
import websocket
from datetime import datetime, timezone

# ─────── USER SETTINGS ─────────────────────────────────────────────────────
BACKEND_WS = "ws://localhost:8082"          # adapt if your backend runs elsewhere
EDGE_ID    = "edge0"
USERNAME   = "admin"
PASSWORD   = "admin"

# List the channels you want to fetch here:
CHANNELS = [
    "meter0/ActiveEnergy",
    "meter0/Voltage",
    # add more channel-IDs as needed...
]
# ────────────────────────────────────────────────────────────────────────────

# --- JSON-RPC IDs ----------------------------------------------------------
AUTH_ID  = str(uuid.uuid4())   # for authenticateWithPassword
OUTER_ID = str(uuid.uuid4())   # for the outer edgeRpc call
INNER_ID = str(uuid.uuid4())   # for the inner getEdgesChannelsValues
# ────────────────────────────────────────────────────────────────────────────

def on_open(ws):
    """Authenticate as soon as the socket opens."""
    auth_payload = {
        "jsonrpc": "2.0",
        "id": AUTH_ID,
        "method": "authenticateWithPassword",
        "params": {
            "username": USERNAME,
            "password": PASSWORD
        }
    }
    ws.send(json.dumps(auth_payload))

def on_message(ws, raw):
    """Handle incoming messages: first auth, then our edgeRpc call, then result."""
    msg = json.loads(raw)

    # 1️⃣  After successful auth, send our getEdgesChannelsValues via edgeRpc
    if msg.get("id") == AUTH_ID and "result" in msg:
        rpc_payload = {
            "jsonrpc": "2.0",
            "id": OUTER_ID,
            "method": "edgeRpc",
            "params": {
                "edgeId": EDGE_ID,
                "payload": {
                    "jsonrpc": "2.0",
                    "id": INNER_ID,
                    "method": "getEdgesChannelsValues",
                    "params": {
                        "ids":      [EDGE_ID],
                        "channels": CHANNELS
                    }
                }
            }
        }
        ws.send(json.dumps(rpc_payload))

    # 2️⃣  Once we get the edgeRpc reply, print its 'result' and exit
    elif msg.get("id") == OUTER_ID:
        print(json.dumps(msg.get("result"), indent=2))
        ws.close()

    # 3️⃣  Any error at any point → print and exit
    elif msg.get("error"):
        print("RPC Error:", msg["error"])
        ws.close()

def on_error(ws, err):
    print("WebSocket error:", err)

def on_close(ws, *_):
    print("Connection closed.")

if __name__ == "__main__":
    websocket.enableTrace(False)
    ws = websocket.WebSocketApp(
        BACKEND_WS,
        on_open    = on_open,
        on_message = on_message,
        on_error   = on_error,
        on_close   = on_close
    )
    ws.run_forever()

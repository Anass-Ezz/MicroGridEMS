#!/usr/bin/env python3
"""
Query daily energy (Wh/kWh) values from OpenEMS via JSON-RPC.

✱ Requires:   pip install websocket-client
✱ Usage:      python query_energy_per_day.py
"""
import json
import uuid
import websocket
from datetime import datetime, timezone

# ─────── USER SETTINGS ─────────────────────────────────────────────────────
BACKEND_WS = "ws://localhost:8082"          # <─ adapt if your backend runs elsewhere
EDGE_ID    = "edge0"
USERNAME   = "admin"
PASSWORD   = "admin"

FROM_DATE  = "2025-03-30"                   # inclusive, UTC date  YYYY-MM-DD
TO_DATE    = "2025-07-30"                   # inclusive, UTC date
TIMEZONE   = "Africa/Casablanca"            # Olson/IANA identifier
# ────────────────────────────────────────────────────────────────────────────

# --- JSON-RPC IDs ----------------------------------------------------------
AUTH_ID  = str(uuid.uuid4())   # authenticateWithPassword
OUTER_ID = str(uuid.uuid4())   # edgeRpc envelope
INNER_ID = str(uuid.uuid4())   # queryHistoricTimeseriesEnergyPerPeriod

def on_open(ws):
    """Authenticate as soon as the socket is open."""
    ws.send(json.dumps({
        "jsonrpc": "2.0",
        "id": AUTH_ID,
        "method": "authenticateWithPassword",
        "params": { "username": USERNAME, "password": PASSWORD }
    }))

def on_message(ws, raw):
    """React to incoming messages."""
    msg = json.loads(raw)

    # 1️⃣  login succeeded → send historic-energy request
    if msg.get("id") == AUTH_ID and "result" in msg:
        ws.send(json.dumps({
            "jsonrpc": "2.0",
            "id": OUTER_ID,
            "method": "edgeRpc",
            "params": {
                "edgeId": EDGE_ID,
                "payload": {
                    "jsonrpc": "2.0",
                    "id": INNER_ID,
                    "method": "queryHistoricTimeseriesEnergyPerPeriod2",
                    "params": {
                        "fromDate": FROM_DATE,
                        "toDate":   TO_DATE,
                        "channels": [
                            # "pvinverter0/ActivePower",
                            # "windturbine0/ActivePower",
                            "meter0/ActiveEnergy"
                        ],
                        "timezone":  TIMEZONE,
                        "resolution": { "value": 1, "unit": "Months" }
                    }
                }
            }
        }))

    # 2️⃣  got the reply → pretty print, then exit
    elif msg.get("id") == OUTER_ID:
        print(json.dumps(msg.get("result"), indent=2))
        ws.close()

    # 3️⃣  any error → print and exit
    elif msg.get("error"):
        print("RPC Error:", msg["error"])
        ws.close()

def on_error(ws, err): print("WebSocket error:", err)
def on_close(ws, *_):  print("Connection closed.")

if __name__ == "__main__":
    websocket.enableTrace(False)
    websocket.WebSocketApp(
        BACKEND_WS,
        on_open     = on_open,
        on_message  = on_message,
        on_error    = on_error,
        on_close    = on_close
    ).run_forever()



















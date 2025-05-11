import websocket
import json
import uuid

# Generate unique JSON-RPC IDs
AUTH_ID  = str(uuid.uuid4())
OUTER_ID = str(uuid.uuid4())
INNER_ID = str(uuid.uuid4())

def on_open(ws):
    # 1) Authenticate (Dummy/File will accept any credentials here)
    ws.send(json.dumps({
        "jsonrpc": "2.0",
        "id":      AUTH_ID,
        "method":  "authenticateWithPassword",
        "params": {
            "username": "admin",
            "password": "admin"
        }
    }))

def on_message(ws, raw):
    msg = json.loads(raw)

    # 2) Once authenticated, send the edgeRpc → queryHistoricTimeseriesData call
    if msg.get("id") == AUTH_ID and "result" in msg:
        print("✅ Authenticated. Edges:", msg["result"].get("edges"))
        ws.send(json.dumps({
            "jsonrpc": "2.0",
            "method":  "edgeRpc",
            "id":      OUTER_ID,
            "params": {
                "edgeId": "edge0",
                "payload": {
                    "jsonrpc": "2.0",
                    "id":      INNER_ID,
                    "method":  "queryHistoricTimeseriesData",
                    "params": {
                        # 👇 Replace these with the exact channels you logged
                        "channels": [
                            "pvinverter0/ActivePower"
                        ],
                        "fromDate":   "2025-05-10",
                        "toDate":     "2025-05-10",
                        "resolution": {"value": 5, "unit": "Minutes"},
                        "timezone":   "Africa/Casablanca"
                    }
                }
            }
        }))

    # 3) Handle the proxied response to edgeRpc (OUTER_ID)
    elif msg.get("id") == OUTER_ID:
        print("📊 Proxied result:", json.dumps(msg.get("result"), indent=2))
        ws.close()

    # 4) Optionally catch inner errors
    elif msg.get("error"):
        print("❌ RPC Error:", msg["error"])
        ws.close()

def on_error(ws, err):
    print("⚠️ WebSocket error:", err)

def on_close(ws, code, reason):
    print("🔌 Connection closed")

if __name__ == "__main__":
    websocket.enableTrace(False)
    ws = websocket.WebSocketApp(
        "ws://localhost:8082",
        on_open=on_open,
        on_message=on_message,
        on_error=on_error,
        on_close=on_close
    )
    ws.run_forever()

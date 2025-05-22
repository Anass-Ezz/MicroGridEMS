#!/usr/bin/env python3
import json, uuid, websocket
from datetime import datetime, timezone

AUTH_ID  = str(uuid.uuid4())
OUTER_ID = str(uuid.uuid4())
INNER_ID = str(uuid.uuid4())

def on_open(ws):
    ws.send(json.dumps({
        "jsonrpc":"2.0","id":AUTH_ID,"method":"authenticateWithPassword",
        "params":{"username":"admin","password":"admin"}
    }))

def on_message(ws, raw):
    msg = json.loads(raw)

    if msg.get("id") == AUTH_ID and "result" in msg:
        today_iso = datetime.now(timezone.utc).strftime('%Y-%m-%d')  # ← date-only
        ws.send(json.dumps({
            "jsonrpc":"2.0","id":OUTER_ID,"method":"edgeRpc",
            "params":{
              "edgeId":"edge0",
              "payload":{
                "jsonrpc":"2.0","id":INNER_ID,"method":"queryHistoricTimeseriesData",
                "params":{
                  "channels":[
                    "pvinverter0/ActivePower",
                    "meter0/ActivePower",
                    "windturbine0/ActivePower"
                  ],
                  "fromDate":"2025-05-10",
                  "toDate": today_iso,                     # ← fixed
                  "resolution":{"value":1,"unit":"Hours"},
                  "timezone":"Africa/Casablanca"
                }
              }
            }
        }))

    elif msg.get("id") == OUTER_ID:
        print(json.dumps(msg.get("result"), indent=2))
        ws.close()

    elif msg.get("error"):
        print("RPC Error:", msg["error"])
        ws.close()

def on_error(ws, err):   print("WebSocket error:", err)
def on_close(ws, *_):    print("Connection closed")

if __name__ == "__main__":
    websocket.enableTrace(False)
    websocket.WebSocketApp(
        "ws://localhost:8082",
        on_open=on_open, on_message=on_message,
        on_error=on_error, on_close=on_close
    ).run_forever()

import asyncio
import websockets
import json
import base64
import uuid

# Configuration
URI = "ws://localhost:8076"
USERNAME = "admin"
PASSWORD = "admin"

# Helper function to create authentication headers
def create_auth_headers(username, password):
    credentials = f"{username}:{password}"
    encoded_credentials = base64.b64encode(credentials.encode()).decode()
    return {
        "Authorization": f"Basic {encoded_credentials}"
    }

# Function to get the status of an edge
async def get_edge_status(edge_ids):
    headers = create_auth_headers(USERNAME, PASSWORD)
    async with websockets.connect(URI, additional_headers=headers) as websocket:
        request = {
            "jsonrpc": "2.0",
            "id": str(uuid.uuid4()),
            "method": "getEdgesStatus",
            "params": {
                "edgeIds": edge_ids
            }
        }
        await websocket.send(json.dumps(request))
        response = await websocket.recv()
        print("GetEdgesStatus Response:", json.loads(response))


# Function to get the status of an edge
async def get_edge_config():
    headers = create_auth_headers(USERNAME, PASSWORD)
    async with websockets.connect(URI, additional_headers=headers) as websocket:
        request = {
            "jsonrpc": "2.0",
            "id": str(uuid.uuid4()),
            "method": "getEdgeConfig",
            "params": {
            }
        }
        await websocket.send(json.dumps(request))
        response = await websocket.recv()
        print("GetEdgesConfig Response:", json.loads(response))


# Function to get the status of an edge
async def write_in_channel():
    headers = create_auth_headers(USERNAME, PASSWORD)
    async with websockets.connect(URI, additional_headers=headers) as websocket:
        request = {
            "jsonrpc": "2.0",
            "id": str(uuid.uuid4()),
            "method": "edgeRpc",
            "params": {
                "edgeId": "edge0",
                "payload": {
                    "jsonrpc": "2.0",
                    "id": str(uuid.uuid4()),
                    "method": "setChannelValue",
                    "params": {
                        "componentId": "ctrlio.openems.edge.controller.demo0",
                        "channelId": "TestChannel",
                        "value": 4000
                    }
                }
            }
        }
        await websocket.send(json.dumps(request))
        response = await websocket.recv()
        print("WriteChannel Response:", json.loads(response))




# Function to subscribe to edge channel updates
async def subscribe_edges_channels(edge_ids, channels):
    headers = create_auth_headers(USERNAME, PASSWORD)
    async with websockets.connect(URI, additional_headers=headers) as websocket:
        # Create the subscription request
        request = {
            "jsonrpc": "2.0",
            "id": str(uuid.uuid4()),
            "method": "subscribeEdgesChannels",
            "params": {
                "count": 0,  # Increment this for each new subscription
                "ids": edge_ids,
                "channels": channels
            }
        }
        await websocket.send(json.dumps(request))
        print(f"Subscribed to channels: {channels} for edges: {edge_ids}")

        # Listen for incoming notifications
        try:
            while True:
                response = await websocket.recv()
                data = json.loads(response)
                if data.get("method") == "edgesCurrentData":
                    params = data.get("params", {})
                    for edge, channel_data in params.items():
                        print(f"Update from {edge}:")
                        for channel, value in channel_data.items():
                            print(f"  {channel}: {value}")
        except websockets.ConnectionClosed:
            print("Connection closed")


# Main function to run the example
async def main():
    edge_ids = ["edge0"]
    channels = ["meter0/ActivePower"]
    # await get_edge_status(edge_ids)
    await write_in_channel()
    # await subscribe_edges_channels(edge_ids, channels)


# Run the main function
if __name__ == "__main__":
    asyncio.run(main())

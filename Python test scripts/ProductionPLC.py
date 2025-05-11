#!/usr/bin/env python3
import random
import time
from pymodbus.client import ModbusTcpClient

def simulate_production():
    # Connect to Modbus TCP server (change IP/port as needed)
    client = ModbusTcpClient('127.0.0.1', port=502)
    
    try:
        # Read the current values of the registers before starting the simulation
        result = client.read_holding_registers(address=0, count=3, slave=3)
        if result.isError():
            print(f"Error reading registers: {result}")
            return

        # Initialize counters with the current values of the registers
        product_count = result.registers[0]  # Register 0: count of products produced
        good_count = result.registers[1]     # Register 1: count of good products
        bad_count = result.registers[2]      # Register 2: count of bad products

        print(f"Initial product count: {product_count}, Good products: {good_count}, Bad products: {bad_count}")

        while True:
            # Simulate product production every 2 seconds
            time.sleep(4)

            # Increment the total product counter (Register 0)
            product_count += 1
            client.write_register(address=0, value=product_count, slave=3)
            print(f"Produced product {product_count}")

            # Simulate whether the product is good or bad
            if random.random() < 0.8:  # 80% chance for good product
                good_count += 1
                client.write_register(address=1, value=good_count, slave=3)
                print(f"Product {product_count} is good.")
            else:
                bad_count += 1
                client.write_register(address=2, value=bad_count, slave=3)
                print(f"Product {product_count} is bad.")
            
    except Exception as e:
        print(f"Error: {e}")
    finally:
        client.close()

if __name__ == "__main__":
    simulate_production()

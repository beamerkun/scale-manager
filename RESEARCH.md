### AEG PW5653 BT

## Original application analysis

# Initial observations

For scale to provide all the more advanced parameters, needs to have:

* gender,
* age,
* height

Weight result can be provided in either kilograms, pounds or stones.

# Bluetooth logs

Using developer setting that allows to store bluetooth communication logs,
It can be easily proven that all needed information is send as one message to
the device.

The result is also sent as one message.

All the communication made using Bluetooth Low Energy standard.

Additionally, analysis of system logs shows that notifications are activated for
1a2ea400-75b9-11e2-be05-0002a5d5c51b characteristic

# Dissasembled code

Among all the obfuscated code, you can find a few Bluetooth Low Energy
characteristics/descriptors UUIDs stored as static strings:

* 00002902-0000-1000-8000-00805f9b34fb
* f433bd80-75b8-11e2-97d9-0002a5d5c51b
* 1a2ea400-75b9-11e2-be05-0002a5d5c51b
* 29f11080-75b9-11e2-8bf6-0002a5d5c51b
* 23b4fec0-75b9-11e2-972a-0002a5d5c51b

## Experiments results

An version of app was created to simply establish bluetooth connection to
the device and then list all services/characteristics/descriptors avaliable.

service 1 : 78667579-7b48-43db-b8c5-7928a6b0a335
  char: 78667579-a914-49a4-8333-aa3c0cd8fedc
   prop: 8 Write
service 2 : f433bd80-75b8-11e2-97d9-0002a5d5c51b
  char: 1a2ea400-75b9-11e2-be05-0002a5d5c51b
   prop: 18 Notify + Read
  char: 23b4fec0-75b9-11e2-972a-0002a5d5c51b
   prop: 18 Notify + Read
  char: 29f11080-75b9-11e2-8bf6-0002a5d5c51b
   prop: 8 Write
service 3 : 78667579-0e7c-45ac-bb53-5279f8ee16fc
  char: 78667579-db57-4c4a-8330-183d7d952170
   prop: 10 Write + Read
  char: 78667579-5605-4f75-8e54-fceb7ea465a9
   prop: 10 Write + Read
  char: 78667579-d0fd-4b77-9515-d03224220c29
   prop: 10 Write + Read
  char: 78667579-e255-4c76-8a12-7be9b176e551
   prop: 10 Write + Read
  char: 78667579-8a38-4775-922d-85c5ccf921c0
   prop: 18 Notify + Read
  char: 78667579-ae48-4e5b-ae14-b8eb728398ec
   prop: 10 Write + Read
  char: 78667579-5773-439a-bbcd-7672550a181b
   prop: 10 Write + Read
service 4 : 78667579-b465-4ef3-a5c6-e8d9bc6c3f8f
  char: 78667579-a914-49a4-8333-aa3c0cd8fedc
   prop: 8 Write


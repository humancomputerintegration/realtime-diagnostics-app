## DEPRECIATED - DELETE

import serial # import Serial Library
import numpy  # Import numpy
 
store = []
port_linux = '/dev/ttyACM0'
arduino_port= serial.Serial(port_linux, 115200) #Creating our serial object named arduinoData
cnt=0
print("initializing")
while True: 
	while (arduino_port.inWaiting() == 0):
		pass

	raw_data = arduino_port.readline()
	processed_data = raw_data.decode('utf-8').strip()
	data_tag = processed_data[:6]
	if(tcmp == "MESG::" or tcmp == "FROM::"):
		info = info + (processed_data[6:], ) #singleton tuple
		info_counter += 1
		# print(processed_data[ign:])

	if(info_counter == 2):
		print(info)
		store.append(info)
		info_counter = 0 
		info = tuple()

	while(info):
		#Write code that will send the message we want here
		send_num = (info.pop(0))[0]
		send_msg = "we received your text - processing the information!"
		

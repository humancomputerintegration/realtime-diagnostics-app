import serial # import Serial Library
import numpy  # Import numpy
 
messages= []
arduinoData = serial.Serial('com3', 115200) #Creating our serial object named arduinoData
cnt=0
print("initializing")
while True: 
    while (arduinoData.inWaiting()==0): #Wait here until there is data
    	pass
    arduinoString = arduinoData.readline()
    process_s = arduinoString.decode('utf-8').strip()
    tcmp = process_s[:6]
    if(tcmp == "DING::" or tcmp == "DONG::"):
    	print(process_s[6:])
import serial
from curses import ascii
# since we need ascii code from CTRL-Z
import time

# here we are testing sending an SMS via virtual serial port ttyUSB0 that was created by a USB serial modem

phonenumber = "+13122410651"
SMS = "here's your SMS!"
ser = serial.Serial('/dev/ttyUSB0', baudrate=9600, timeout=1, dsrdtr = True, rtscts =True)
# 460800 is baud rate, ttyUSB0 is virtual serial port we are sending to
ser.write(str.encode("AT\r\n"))
# send AT to the ttyUSB0 virtual serial port
line = ser.readline().decode(encoding='UTF-8')
print(line)
# what did we get back from AT command? Should be OK
ser.write(str.encode("AT+CMGF=1\r\n"))
# send AT+CMGF=1 so setting up for SMS followed by CR
line = ser.readline()
print(line)
# what did we get back from that AT command?
ser.write(str.encode('AT+CMGS="%s"\r\n' %phonenumber))
# send AT+CMGS then CR, then phonenumber variable
ser.write(str.encode(SMS))
# send the SMS variable after we sent the CR
ser.write(str.encode(ascii.ctrl('z')))
# send a CTRL-Z after the SMS variable using ascii library
# time.sleep(10)
# wait 10 seconds
print(ser.readline())
print(ser.readline())
print(ser.readline())
print(ser.readline())
# what did we get back after we tried AT_CMGS=phonenumber followed
# by <CR> , then SMS variable, then <CTRL-Z> ascii code??

#!/usr/bin/env python3
import json 
from databases import mongo_wrapper as mw
import serial
import threading

#The ardunio should be flashed with the arduino file "sms_recv.ino"
def sms_listener(port_name="/dev/ttyACM0", baud_rate=115200, ign=6, filter_key=["MESG::","FROM::"]):
	print("starting the SMS listener")

	db_client = mw.open_connection('localhost',27017,'root',"humancomputerintegration")
	collection = prepare_collection(db_client, "proto_test_db", "proto_test_coll")


	running = True
	arduino_port = serial.Serial(port_name, baud_rate)
	info_counter = 0 
	info = tuple()
	while (running):
		while (arduino_port.inWaiting() == 0):
			pass

		raw_data = arduino_port.readline()
		processed_data = raw_data.decode('utf-8').strip()
		data_tag = processed_data[:ign]

		if(data_tag in filter_key):
			info = info + (processed_data[ign:], ) #singleton tuple
			info_counter += 1
			# print(processed_data[ign:])

		if(info_counter == 2):
			print(info)
			info_counter = 0 
			info = tuple()

	mw.close_connection(db_client)


def prepare_collection(client, dbname: str, coll_name:str):
	db = mw.create_db(client, dbname)
	if(db == None):
		db = mw.get_db(client, dbname)

	collection = mw.create_collection(client, coll_name)
	if(collection == None):
		collection = mw.get_collection(client, coll_name)

	return collection

def process_data(collection, raw_text):
	info = raw_text.split(";")
	pid = info[0]
	symp = info[1]
	dis = info[2]
	return 0
	
def store_data(processed_data:dict):
	mw.insert(client, processed_data)
	return True


def process_text(text):
	
	running = True
	arduino_port = serial.Serial() #port_name, baud_rate
	print(text)
	return;

if __name__ == "__main__":
	print("DOes nothing when called")
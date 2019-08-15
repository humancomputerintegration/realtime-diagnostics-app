#!/usr/bin/env python3
import json 
from databases import mongo_wrapper as mw
import serial
import threading
import os
import datetime

#The ardunio should be flashed with the arduino file "sms_recv.ino"
#Todo make sure you implement a usage with **kwargs - which might help reduce the number of functions we need
def sms_listener(port_name="/dev/ttyACM0", baud_rate=115200, ign=5, filter_key=["MMEI:","FROM:"]):
	db_client = mw.open_connection('localhost',27017,'root',"humancomputerintegration")
	db_collection = prepare_collection(db_client, "mobilemedicine_test_db", "data_test_coll")
	umls_to_medt, ind_to_dumls, ind_to_sumls = process_dictionaries('medical_assets/DiseaseList.csv',
																	'medical_assets/SymptomList.csv')

	running = True
	arduino_port = serial.Serial(port_name, baud_rate)

	print("Starting the SMS listener")
	from_pnum = None 
	raw_text = None

	while (running):
		while (arduino_port.inWaiting() == 0):
			pass

		raw_data = arduino_port.readline()
		processed_data = raw_data.decode('utf-8').strip()
		data_tag = processed_data[:ign]

		if(data_tag in filter_key):
			if(data_tag == filter_key[0]):
				raw_text = processed_data[5:]
			elif(data_tag == filter_key[1]):
				from_pnum = processed_data[5:]
			else:
				print("error")

			print(raw_text, " ==", from_pnum)

			if(from_pnum != None and raw_text != None):
				pthread = threading.Thread(target=process_signal, 
					args=(db_collection,from_pnum,raw_text,umls_to_medt,ind_to_dumls, ind_to_sumls))
				pthread.start()
				# sd = process_data(db_collection,from_pnum,raw_text,umls_to_medt, 
				# 										ind_to_dumls, ind_to_sumls)
				# mw.insert(db_collection, sd)
				from_pnum = None
				raw_text = None

	mw.close_connection(db_client)

def process_signal(db_collection,from_pnum,raw_text,umls_to_medt,ind_to_dumls, ind_to_sumls):
	sd = process_data(db_collection,from_pnum,raw_text,umls_to_medt,ind_to_dumls, ind_to_sumls)
	mw.insert(db_collection,sd)
	print("THREAD DONE")


def process_dictionaries(umlsdis, umlssymp):
	umls_to_medical_term = dict()
	ind_to_dis_umls = dict()
	ind_to_symp_umls = dict()

	with open(umlsdis, "r") as f:
		f.readline() #skip the header in the document
		dis_counter = 0 
		for line in f:
			dis_pair = line.split(',')
			ind_to_dis_umls[dis_counter] = dis_pair[0].strip()
			umls_to_medical_term[dis_pair[0].strip()] = dis_pair[1].strip()
			dis_counter = dis_counter + 1

	with open(umlssymp, "r") as g: 
		g.readline() #skip the header in the document 
		symp_counter = 0
		for line in g:
			symp_pair = line.split(',')
			ind_to_symp_umls[symp_counter] = symp_pair[0].strip()
			umls_to_medical_term[symp_pair[0].strip()] = symp_pair[1].strip()
			symp_counter = symp_counter + 1

	return umls_to_medical_term, ind_to_dis_umls, ind_to_symp_umls

def prepare_collection(client, dbname: str, coll_name:str):
	db = mw.create_db(client, dbname)
	if(db == None):
		db = mw.get_db(client, dbname)

	collection = mw.create_collection(db, coll_name)
	if(collection == None):
		collection = mw.get_collection(db, coll_name)

	return collection

def process_data(collection, source, raw_text, umls_to_medt, ind_to_dumls, ind_to_sumls):
	ts = datetime.datetime.now().timestamp()

	#Storing source and time related information
	struct_data = dict()
	struct_data["from"] = source
	struct_data["timestamp"] = ts

	#Storing data about the patient
	payload = raw_text.split(";")
	struct_data["patient id"] = int(payload[0])
	struct_data["patient_sex"] = "MALE" if (payload[1] == "M") else "FEMALE"
	struct_data["patient_age"] = int(payload[2])
	struct_data["patient height"] = float(payload[3])
	struct_data["patient weight"] = float(payload[4])

	sumls = []
	sname = []
	for tsym in payload[5].split(","):
		sumls.append(ind_to_sumls[int(tsym)])
		sname.append(umls_to_medt[ind_to_sumls[int(tsym)]])

	dumls = ind_to_dumls[int(payload[6])]
	dname = umls_to_medt[ind_to_dumls[int(payload[6])]]

	struct_data["symptoms_umls"] = sumls
	struct_data["symptoms_name"] = sname
	struct_data["diagnosis_umls"] = dumls
	struct_data["diagnosis_name"] = dname
	
	return struct_data 

if __name__ == "__main__":
	sms_listener()
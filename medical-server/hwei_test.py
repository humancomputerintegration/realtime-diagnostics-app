import huaweisms.api.user
import huaweisms.api.wlan
import huaweisms.api.sms
import time
import mongo_wrapper as mw
import datetime
import time
import os

def sms_lisener_huawei(usercred="admin",userpass="admin", parts=3):
	db_client = mw.open_connection('localhost',27017,'admin',"mobilemedicine")
	db_collection = prepare_collection(db_client, "mobilemedicine_test_db", "data_test_coll")
	umls_to_medt, ind_to_dumls, ind_to_sumls = process_dictionaries('medical_assets/DiseaseList.csv',
																	'medical_assets/SymptomList.csv')

	print("Starting the server")
	# Establish SMS listener access point
	try:
		ctx = huaweisms.api.user.quick_login(usercred, userpass)
	except ValueError:
		print("Invalid Login Credentials")
		return 	
	except:
		print("unknown login error -- is the adapter plugged in?")

	running = True
	print("Starting the SMS listener")
	from_pnum = None 
	raw_text = None
	
	while(running):
		z = huaweisms.api.sms.get_sms(ctx,box_type=1,page=1,qty=10,unread_preferred=True)
		if(int(z['response']['Count']) == 0):
			time.sleep(1)
			continue

		tracked = []
		for txtmsg in (z['response']['Messages']['Message']):
			from_pnum = txtmsg['Phone']
			raw_text = txtmsg['Content']
			text_index = txtmsg['Index']
			print(from_pnum, text_index)
			sd = process_data(db_collection,from_pnum,raw_text,umls_to_medt, 
													ind_to_dumls, ind_to_sumls)
			mw.insert(db_collection, sd)
			from_pnum = None
			raw_text = None
			huaweisms.api.sms.delete_sms(ctx, text_index)
		
		time.sleep(2)

	mw.close_connection(db_client)
	return True

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

# ctx = huaweisms.api.user.quick_login("admin", "admin")

# debug_counter = 0 

# while(True):
# 	if(debug_counter % 1000 == 0):
# 		print("debug counter =", int(debug_counter/1000))

# 	debug_counter += 1
# 	z = huaweisms.api.sms.get_sms(ctx,box_type=1,page=1,qty=10,unread_preferred=True)
# 	if(int(z['response']['Count']) == 0):
# 		time.sleep(1)
# 		continue

# 	tracked = []
# 	for x in (z['response']['Messages']['Message']):
# 		print(x['Phone'], x['Content'], x['Index'])
# 		huaweisms.api.sms.delete_sms(ctx, x['Index'])
	
# 	time.sleep(2)

if __name__ == "__main__":
	sms_lisener_huawei()
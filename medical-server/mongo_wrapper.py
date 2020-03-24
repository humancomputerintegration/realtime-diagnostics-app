#Pymongo wrapper to help further abstract database construction in MongoDB
import pymongo
from pprint import pprint 
from pymongo.uri_parser import parse_host 

#https://api.mongodb.com/python/current/api/pymongo/database.html

#Client Functions 
def open_connection(host:str, port:int, username, password):
	base = "mongodb://{}:{}@{}:{}/?authSource=admin&ssl=false"
	uri = base.format(username,password,host,port)
	# client = pymongo.MongoClient(uri,serverSelectionTimeoutMS=100)
	client = pymongo.MongoClient(host=host, port=port,
		username=username, password=password, authSource='admin')
	try:
		server_details = client.server_info()
	except pymongo.errors.ServerSelectionTimeoutError as e:
		print(e)
		print("Possible Errors: Credentials, Host & Port, Internet Connection")	

	return client

def close_connection(client):
	client.close()

def admin_command(client, cmd):
	print("beginning admin command")
	return client.admin.command(cmd)

#Database & User Functions
def list_dbs(client, mode = 'names'):
	valid_lists = {'names': client.list_database_names(),
					'cursors': client.list_databases()}
	return valid_lists.get(mode, ValueError('Input mode is not valid'))

# Creates and returns a new DB 
# Note: A database will not be created until there is at 
# least one record in the DB - so we create a dummy collection
def create_db(client, new_database):
	# print(client.list_databases_names())
	if new_database in client.list_database_names():
		print("Database already exists -- returning none")
		return None 
	print("finished creating database ::", new_database)
	client[new_database]
	return client[new_database]

#checks if a DB exists and returns it
def get_db(client, database_name):
	if not (database_name in client.list_database_names()):
		print("database is not in client - function ending")
		return None;
	print("returning ::", database_name)
	return client[database_name]

def drop_db(client,database_name):
	if not (database_name in client.list_database_names()):
		print("database is not in the client - function ending")
		return;
	print("dropping db :: ", database_name)
	client.drop_database(database_name)
	print(client.list_database_names())
	return;

def drop_dbs(client, database_names):
	if(isinstance(database_names, list)):
		for name in database_names:
			print("Droppping ::", name)
			drop_db(client, name)
		print("finished dropping all of the databases")
		return;

	print("Not a valid list of database names")
	return;

def createUser(database, username, password, hierarchy="user",roles=["read"]):
	database.command("createUser", hierarchy, pwd = password, roles=roles)
	print("Created {} with {} and {} roles".format(username, hierarchy, roles))

def updateUser(database, username, password, hierarchy="user",roles=["read"]):
	datbase.command("updateUser", hierarchy, pwd = password, roles=roles)
	print("updated user")

#This function will only create a temporary collection until a document is inserted
def create_collection(database, new_collection):
	if (new_collection in database.list_collection_names()):
		print("Collection already exists - function ending")	
		return None;
	
	return database[new_collection]

def get_collection(database, collection_name):
	if not (collection_name in database.list_collection_names()):
		print("Collection does not exist in this database -exitting")
		return None
	return database[collection_name]

def drop_collection(database, collection_name):
	if not (collection_name in database.list_collection_names()):
		print("Collection doesn't exist in this database - exitting")
		return;
	database.drop_collection(collection_name)
	print("Post drop ::",database.list_collection_names())
	return;

def insert(collection, docs, many = False):
	if(many):
		print("Inserting multiple documents into the collection", end = "")
		collection.insert_many(docs)
	else:
		print("Inserting one document into the collection", end ="")
		collection.insert_one(docs)
	print("-----> done")
	 
def unit_tests():
	print("beginning unit tests for mongo_wrapper.py")
	print("------------------------------------------------")

def bad_test():
	print("starting the tests")
	client = open_connection('localhost',27017,'admin','mobilemedicine')
	# pprint(admin_command(client, "serverStatus"))

	temp_db = create_db(client, "test2_database") # temp_db = client["test2_database"]
	temp_db2 = create_db(client, "test3_database") 	# temp_db2 = client["test3_database"]

	print(list_dbs(client, mode='names'))

	# stuff = get_db(client, "test2_database")
	# stuff2 = get_db(client, "test3_database")

	tcoll1 = create_collection(temp_db, "testcollection")
	tcoll2 = create_collection(temp_db2, "oiwjeroiwjrowirj")
	
	post_data = {
		'title':'Python and MongoDB',
		'content':'OIJOIJOIJ',
		'author':'ted'
	}

	post1= {
		'title':'AA',
		'content':'BB',
		'author':'CC'
	}

	post2= {
		'title':'DD',
		'content':'EE',
		'author':'FF'
	}

	post3= {
		'title':'GG',
		'content':'HH',
		'author':'II'
	}

	insert(tcoll1, post_data, False)
	insert(tcoll2, [post1,post2,post3], True)

	# drop_collection(stuff, "dummycollection")
	# drop_collection(stuff2, "dummycollection")

if __name__ == "__main__":
	bad_test()
#Pymongo wrapper to help further abstract database construction in MongoDB
import pymongo
from pprint import pprint 
from pymongo.uri_parser import parse_host 

#https://api.mongodb.com/python/current/api/pymongo/database.html


#create_collection, more robust collection creation 
#This method should only be used to specify options on creation

#current_op = returns the current setion 
#drop_collection(name_or_collection, session=None)
#list_collections(session=None, filter=None, **kwargs)
#command - very important = Issue a MongoDB command.

#db.command("buildinfo")
#db.command("collstats", collection_name)
#db.command("filemd5", object_id, root=file_root) 

#Client Functions 
def open_connection(host:str, port:int, username, password):
	uri = "mongodb://{}:{}@{}:{}/".format(username,password,host,port)
	client = pymongo.MongoClient(uri,serverSelectionTimeoutMS=40)
	try:
		server_details = client.server_info()
		# print("Connection Successful. Server details below:")
		# print("------------------------------------------------")
		# pprint(server_details)	
		# print("------------------------------------------------")
	except pymongo.errors.ServerSelectionTimeoutError as e:
		print(e)
		print("Possible Errors: Credentials, Host & Port, Internet Connection")	

	return client

def close_connection(client):
	client.close()

#Database & User Functions

def list_dbs(client, mode = 'names'):
	valid_lists = {'names': client.list_database_names(),
					'cursors': client.list_databases()}
	return valid_lists.get(mode, ValueError('Input mode is not valid'))

# Creates and returns a new DB 
# Note: A database will not be created until there is at 
# least one record in the DB - so we create a dummy collection
def create_db(client, new_database):
	if new_database in client.list_database_names():
		print("Database already exists -- returning none")
		return None 
	temp_coll = client[new_database].testcollection.insert_one({'DUMMY':0})
	print("finished creating database::", new_database)
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
	
def createUser(database, username, password, hierarchy="user",roles=["read"]):
	database.command("createUser", hierarchy, pwd = password, roles=roles)
	print("Created {} with {} and {} roles".format(username, hierarchy, roles))

def updateUser(database, username, password, hierarchy="user",roles=["read"]):
	datbase.command("updateUser", hierarchy, pwd = password, roles=roles)
	print("updated user")


def insert_record(client, database_name, collection_name, records):
	if(records == None):


def create_collection(client, database_name, new_collection):

# def get_collection(collection_name):

# def insert_document():
# def find_document(query requirements):
 
def unit_test():
	print("beginning unit tests for mongo_wrapper.py")
	print("------------------------------------------------")


if __name__ == "__main__":
	print("starting the tests")
	client = open_connection('localhost',27017,'root',"humancomputerintegration")
	# serverStatusResult=client.admin.command("serverStatus")
	# pprint(serverStatusResult)

	temp_db = create_db(client, "test2_database")
	temp_db2 = create_db(client, "test3_database")
	# temp_db = client["test2_database"]
	# temp_db2 = client["test3_database"]

	print(list_dbs(client, mode='names'))
	drop_db(client, "test2_database")
	drop_db(client, "test3_database")
	# print(list_dbs(client, mode='cursors'))




	
	# posts = temp_db.posts #we need to use this .notation to make sure it's there 
	# posts2 = temp_db2.posts

	# post_data = {
	# 	'title':'Python and MongoDB',
	# 	'content':'OIJOIJOIJ',
	# 	'author':'ted'
	# }

	# result = posts.insert_one(post_data)
	# result = posts2.insert_one(post_data)
	# # print("one post: {}".format(result.inserted_id))

	# post_1= {
	# 	'title':'AA',
	# 	'content':'BB',
	# 	'author':'CC'
	# }

	# post_2= {
	# 	'title':'DD',
	# 	'content':'EE',
	# 	'author':'FF'
	# }

	# post_3= {
	# 	'title':'GG',
	# 	'content':'HH',
	# 	'author':'II'
	# }

	# new_result = posts.insert_many([post_1, post_2, post_3])
	# new_result = posts2.insert_many([post_1, post_2, post_3])

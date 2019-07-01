import pymongo
from pprint import pprint 

from pymongo.uri_parser import parse_host 

#https://api.mongodb.com/python/current/api/pymongo/database.html
# Things to implement

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

#Database Functions
def createUser(database, username, password, hierarchy="user",roles=["read"]):
	database.command("createUser", hierarchy, pwd = password, roles=roles)

def updateUser(database, username, password, hierarchy="user",roles=["read"]):
	datbase.command("updateUser", hierarchy, pwd = password, roles=roles)

def drop_db(client,database_name):
	client.drop_db(database_name)


# def get_create_db:

# def get_db():

def list_dbs(client, mode = 'names'):
	valid_lists = {'names': client.list_database_names(),
					'cursors': client.list_databases()}
	throw_error = lambda : ValueError('Not a mode')
	return valid_lists.get(mode, throw_error)
	
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

	print(list_dbs(client, mode='names'))
	print(list_dbs(client, mode='cursors'))

	

	# temp_db = client["test2_database"]
	# temp_db2 = client["test3_database"]

	
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

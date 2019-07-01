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

def open_connection(host:str, port:int, username, password):
	uri = "mongodb://{}:{}@{}:{}/".format(username,password,host,port)
	client = pymongo.MongoClient(uri,serverSelectionTimeoutMS=40)
	try:
		server_details = client.server_info()
		print("Connection Successful. Server details below:")
		print("------------------------------------------------")
		pprint(client.server_info())	
	except pymongo.errors.ServerSelectionTimeoutError as e:
		print(e)
		print("Possible Errors: Credentials, Host & Port, Internet Connection")	

	return client

def createUser(database, username, password, hierarchy="user",roles=["read"]):
	database.command("createUser", hierarchy, pwd = password, roles=roles)

def updateUser(database, username, password, hierarchy="user",roles=["read"]):
	datbase.command("updateUser", hierarchy, pwd = password, roles=roles)
	
# def pick_db(db_name):

# def get_collection(collection_name):

# def insert_document():

# def find_document(query requirements):
 	
if __name__ == "__main__":
	print("starting the tests")
	client = open_connection('localhost',27017,'root',"humancomputerintegration")
	# serverStatusResult=client.admin.command("serverStatus")
	# pprint(serverStatusResult)

	temp_db = client["test2_database"]
	posts = temp_db[posts2] #we need to use this .notation to make sure it's there 

	# post_data = {
	# 	'title':'Python and MongoDB',
	# 	'content':'OIJOIJOIJ',
	# 	'author':'ted'
	# }

	# result = posts.insert_one(post_data)
	# print("one post: {}".format(result.inserted_id))

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
	# print("multiple hosts: {}".format(new_result.inserted_ids))
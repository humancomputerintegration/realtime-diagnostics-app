from pymongo import MongoClient


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


def open_connection():
	parse_host(uri to be used here)
	client = MongoClient('mongodb://loclhost:27017')

def pick_db(db_name):

def get_collection(collection_name):

def insert_document():

def find_document(query requirements):
 	

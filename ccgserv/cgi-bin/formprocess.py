#!/usr/bin/env python3

import cgi
import cgitb
cgitb.enable()
from twilio.rest import Client

form = cgi.FieldStorage()

def send_text_bad(sid,token,message,sendn,recvn):
	client = Client(sid, token)
	message = client.messages.create(body=message,from_=sendn,to=recvn)
	print("message sent (bad method) - message id = {}".format(message.sid))

def send_text(cred_file, message, sendn, recvn):
	tempf = open(cred_file, 'r')
	cred = json.loads(tempf.read())
	tempf.close()

	account_sid = cred["account_sid"]
	auth_token = cred["auth_token"]
	client = Client(account_sid, auth_token)

	message = client.messages.create(body=message,from_=sendn,to=recvn)
	print("message sent - message id = {}".format(message.sid))

try: 
	phone_num = (form["pnum"].value).strip()
	response = form["msg"].value
	mod_num = "+1" + phone_num
	tsid = "ACff003bbe467e6f77330f29e8baf8b6fc"
	ttoken = "3da9fa49b56789fc259b81e32a6d1efe"
	send_text_bad(tsid,ttoken,response,'+12244791518',mod_num)

except:
	print("Something went wrong")

print ("Content-type:text/html\r\n\r\n")
print ("<html>")
print ("<head>")
print ("<title>Hello - Second CGI Program</title>")
print ("</head>")
print ("<body>")
print("<h2>Given Phone number: {} \n </br> Text sent: {} \n </h2>".format(mod_num,response))
print ("</body>")
print ("</html>")

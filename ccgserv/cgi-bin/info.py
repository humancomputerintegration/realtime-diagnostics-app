#!/usr/bin/env python3

import sys 
import site
import cgi
import cgitb
cgitb.enable()


from twilio.rest import Client

form = cgi.FieldStorage()

def send_text_bad(sid,token,message,sendn,recvn):
	client = Client(sid, token)
	message = client.messages.create(body=message,from_=sendn,to=recvn)
	print("message sent (bad method) - message id = {}".format(message.sid))

tsid = "ACff003bbe467e6f77330f29e8baf8b6fc"
ttoken = "3da9fa49b56789fc259b81e32a6d1efe"
mod_num = "+18478686626"
send_text_bad(tsid,ttoken,"more tests",'+12244791518',mod_num)

print ("Content-type:text/html\r\n\r\n")
print ("<html>")
print ("<head>")
print ("<title>Hello - Second CGI Program</title>")
print ("</head>")
print ("<body>")
print("<h2>ONE: {} \n </h2> </br> ".format(sys.version))
print("<h3>TWO: {} \n </h2> </br> ".format(sys.prefix))
print ("</body>")
print ("</html>")

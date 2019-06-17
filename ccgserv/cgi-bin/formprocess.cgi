#!/usr/bin/env python3

import sms as s

import cgi
import cgitb
cgitb.enable()

form = cgi.FieldStorage()

try: 
	phone_num = int(form["pnum"].value)
	response = form["msg"].value
	mod_num = "+1" + phone_num
	s.send_text('../cred.json',response,'+12244791518',mod_num)
except:
	print("Something went wrong")

print ("Content-type:text/html\r\n\r\n")
print ("<html>")
print ("<head>")
print ("<title>Hello - Second CGI Program</title>")
print ("</head>")
print ("<body>")
print("<h2>Given Phone number: {} \n </br> Text sent: {} \n </h2>".format(phone_num,response))
print ("</body>")
print ("</html>")

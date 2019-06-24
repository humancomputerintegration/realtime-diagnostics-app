from flask import Flask, render_template, request
import sms as s

#Threading Modules 
import threading
import logging
import time

app = Flask(__name__)


@app.route("/")
def my_form():
    return render_template('sendtext.html')

@app.route("/", methods = ['GET','POST'])
def my_form_post():
	if request.method == 'GET':
		return render_template('empty.html')
	else:
		pnum = request.form['pnum']
		msg = request.form['msg']
		npnum = "1" + pnum.strip()
		s.send_text('cred.json',msg,'+12244791518',npnum)
		return render_template('textsent.html',npnum = npnum, msg = msg)
		
#allows us to run the webserver w/out environment variables 
if __name__ == '__main__':
	web_serv = threading.Thread(target = app.run(debug=True, threaded = True))
	sms_serv = threading.Thread(target = s.sms_listener())
	# app.run(debug=True, threaded=True)





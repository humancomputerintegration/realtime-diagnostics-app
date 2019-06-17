from flask import Flask, render_template, request
from twilio.rest import Client

app = Flask(__name__)


@app.route("/")
def my_form():
    return render_template('sendtext.html')

@app.route("/", methods = ['POST'])
def my_form_post():
	pnum = request.form['pnum']
	msg = request.form['msg']
	npnum = "1" + pnum.strip()

	tsid = "ACff003bbe467e6f77330f29e8baf8b6fc"
	ttoken = "3da9fa49b56789fc259b81e32a6d1efe"
	client = Client(tsid, ttoken)
	message = client.messages.create(body=msg,from_='+12244791518',to=npnum)
	print("message sent (bad method) - message id = {}".format(message.sid))

	return render_template('textsent.html',npnum = npnum, msg = msg)


#allows us to run the webserver w/out environment variables 
if __name__ == '__main__':
	app.run(debug=True)





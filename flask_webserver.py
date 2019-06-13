from flask import Flask, render_template, request

app = Flask(__name__)


@app.route("/")
def my_form():
    return render_template('my_form.html')

@app.route("/", methods = ['POST'])
def my_form_post():
	text = request.form['text']
	processed_text = text.upper()
	return processed_text


#allows us to run the webserver w/out environment variables 
if __name__ == '__main__':
	app.run(debug=True)





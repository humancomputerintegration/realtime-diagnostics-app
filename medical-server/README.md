# MobileMedicine Server Information

## Explanation of directory structure and files 
The important files within this medical server repository are the encryption module (enc.py), 
SMS server module (sms_handler.py), database module (mongo_wrapper.py), and the keys (.pem files). In addition,
the files that the app and server *should* exist in the "medical_assets" directory (In the future, it would 
be ideal if the app and server build automatically shared the same medical_assets directory).

### "auxillary_information" directory
This directory is just a small directory containing little details/reminders regarding specific information
related to server configuration/setup. These should be moved to the README soon
 
### Arduino Directory & Deleting legacy files
The "ard_files" directory were files used to flash the SMS arduino receiver that was used in earlier iterations
of the project. These can be deleted once use of the arduino module is 100% determined to be gone. The rest of the
files are legacy of earlier iterations of the project - and will be deleted once their importance is 100% determined.

## SMS Adapter(s) for MobileMedicine Server 
The server only supports the "HUAWEI LTE E8372 Hotspot Turbo Stick" as of now. The turbo stick offers a lot of other
features aside from SMS functionality, but for this project, these features will not be leveraged. From my experience, WiFi and the SMS functionallity cannot be done at the same time (I speculate this might be because of the hotspot feature of the 
huawei adapter). Furthermore, to easily abstract the functions of the HUAWEI turbo stick, we use a [python library](https://github.com/pablo/huawei-modem-python-api-client). However, this library is not super critical to accessing SMS features - an individual can access a localhost to a Huawei gui client for SMS. 

## Hardware configurations for MobileMedicine
As of now, there is no "recommended" hardware configuration for the mobilemedicine server (the app component may be
different). Past versions of mobilemedicine have worked fine on a dell optilex and a dell precision laptop running 
Ubuntu 18.04. 

As of now, there is no standard hardware configuration for the mobilemedicine server.


## Software and Operating System configurations
Ideally, we would be able to easily deploy this in a docker container so that MobileMedicine
will be machine agnostic. 
#### Operating System (OS): 
Ubuntu 18.04
#### Programming Languages: 
Python (libraries will be listed on requirements.txt)
#### Software: 
MongoDB, MongoDB's Compass (for Ubuntu)

## Concerns about server functionality going forward: 

### Multi-threading with SMS functionality 
As long encrypted information is sent within a single text message, applying multi-thread support for the server
should be straightforward. However, if we stick with more secure encryption methods that break up our ciphertext
over SMS, then multithreading 


### SMS Security and potential attacks 
To prevent obvious attacks from being done on test deployments, we will keep our security concerns off the public
repo. 


## Unit Testing
Unit Tests are in progress, an automatic test suite would be ideal 


# TODO's 
CRITICAL: Create a docker image that can host the server regardless of platform

ENCRYPTION NOTES: Need to figure out an encryption scheme that indicates uniqueness, to remove potential for easy spoofing 

REFRACTOR CODE - MAKE IT MORE MODULAR

Write some tests & documentation to make the code test and run with the arduino-defined SMS interfacer & Huawei one 

Write a requirements install for both the ubuntu environment and python install
(Potentially setup a virtualenv for python?)

Make an installer 

REMINDER: automatically start "mongod" at the start of every boot, to make recording/inserting processes instant:
This can be done pretty easily with ubuntu


int setPoint = 55; 
String readString; 

void setup() {
  while (!Serial);
  Serial.begin(115200);
//  Serial.setTimeout(10);
}

int incomingByte = 0;


void loop() {
  // send data only when you receive data:
  if (Serial.available() > 0) {// read the incoming byte:
//    incomingByte = Serial.read();
    incomingByte = Serial.parseInt();
    // say what you got:
    Serial.print("I received: ");
    Serial.println(incomingByte);
  }
}

//void loop() {
////  Serial.print("HELLO TED I AM SAYING HI FROM THE ARDUINO"); 
////  delay(1000);
//  
//  while (Serial.available()){
////    delay(30);
//    if(Serial.available() > 0){
//      char c = Serial.read();
//      readString += c; 
//    }
//  }
//  if (readString.length() >0)
//  {
//    Serial.print("Arduino says: ");  
//    Serial.println(readString); //see what was received
////    readString = "";
//  }
//
////  delay(500);
//
////  char ard_sends = '3';
//////  char ard_sends2 = '4';
////  Serial.print("Arduino sends: ");
////  Serial.print(ard_sends);
//////  Serial.println(ard_sends2);
////  Serial.print("\n");
////  Serial.flush();
//}

//SMS Receiver that will be used to flash the firmware
#include "Adafruit_FONA.h"
#include "Wire.h"

#define FONA_RX 2
#define FONA_TX 3
#define FONA_RST 4

#include <SoftwareSerial.h>
SoftwareSerial fonaSS = SoftwareSerial(FONA_TX, FONA_RX);
SoftwareSerial *fonaSerial = &fonaSS;
// Use this for FONA 800 and 808s
Adafruit_FONA fona = Adafruit_FONA(FONA_RST);

char replybuffer[255]; //buffer for replies
uint8_t readline(char *buff, uint8_t maxbuff, uint16_t timeout = 0);

void setup() {
  while (!Serial);

  Serial.begin(115200);
  // make it slow so its easy to read!
  fonaSerial->begin(4800);
  if (! fona.begin(*fonaSerial)) {
    Serial.println(F("Couldn't find FONA"));
    while(1);
  }
  Serial.println(F("FONA is OK"));

  // Print SIM card IMEI number.
  char imei[16] = {0}; // MUST use a 16 character buffer for IMEI!
  uint8_t imeiLen = fona.getIMEI(imei);
  if (imeiLen > 0) {
    Serial.print("SIM card IMEI: "); Serial.println(imei);
  }

//  fonaSerial->print("AT+CNMI=2,1\r\n");  //set up the FONA to send a +CMTI notification when an SMS is received
  Serial.println("DING::FONA Ready");
}
  
char fonaNotificationBuffer[64]; //for notifications from the FONA
char smsBuffer[250];

void loop() {
  char* bufPtr = fonaNotificationBuffer;    //handy buffer pointer
  if (fona.available())      //any data available from the FONA?
  {
    int slot = 0;            //this will be the slot number of the SMS
    int charCount = 0;
    //Read the notification into fonaInBuffer
    do  {
      *bufPtr = fona.read();
      Serial.write(*bufPtr);
      delay(1);
    } while ((*bufPtr++ != '\n') && (fona.available()) && (++charCount < (sizeof(fonaNotificationBuffer)-1)));
    
    //Add a terminal NULL to the notification string
    *bufPtr = 0;

    //Scan the notification string for an SMS received notification.
    //  If it's an SMS message, we'll get the slot number in 'slot'
    if (1 == sscanf(fonaNotificationBuffer, "+CMTI: " FONA_PREF_SMS_STORAGE ",%d", &slot)) {
      char callerIDbuffer[32];  //we'll store the SMS sender number in here
      // Retrieve SMS sender address/phone number.
      fona.getSMSSender(slot, callerIDbuffer, 31);
      //      callerIDbuffer
      // Retrieve SMS value.
      uint16_t smslen;
    
      if (fona.readSMS(slot, smsBuffer, 250, &smslen)) { // pass in buffer and max len!
        Serial.print("MMEI:");
        Serial.println(smsBuffer);
        Serial.print("FROM:");
        Serial.println(callerIDbuffer);
      }
      //Send back an automatic response
      //This process of messing around with the character buffers for a custom 
      //message seems to take a long time, so I might just edit this out

// Replying to stuff
//      char replyBuffer[250];
//      String replyString = String("You sent:: ") + smsBuffer;
//      replyString.toCharArray(replyBuffer, 250);
//      fona.sendSMS(callerIDbuffer, replyBuffer);
      fona.deleteSMS(slot);
    }
  }
}

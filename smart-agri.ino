#include <ESP8266WiFi.h>
#include <SoftwareSerial.h>
#include <ArduinoJson.h>
#include <ESP8266HTTPClient.h>

//FIREBASE method
#include <FirebaseArduino.h>
#define FIREBASE_HOST "siram-project3.firebaseio.com"
#define FIREBASE_AUTH "P56o8HxaHad7daHIpq90RxmY7U8ZVFvdcAOH27mu"
#define WIFI_SSID "Barokah"
#define WIFI_PASSWORD "12345678"

//Waktu
#include <NTPClient.h>
#include <WiFiUdp.h>

//DHT 11 method
#include <DHT.h>
DHT dht(4, DHT11);  //Pin D2 -> DHT11
int analog0 = A0;   //Pin A0 -> Soil Moist
float tentatif;
float value_mois;
float value_temp;
float value_humid;

//Relay
int relay_pin = 14;  //Pin D5 -> relay
int control = 0; //Kondisi Awal
unsigned int amountWater;

//Get Waktu
int WIB = 7*3600;
String jam;
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "europe.pool.ntp.org", WIB, 60000);

//Water Flow
#define PULSE_PIN 5 //pin D1 -> Flow Sensor
volatile long pulseCount = 0; 
float calibrationFactor = 31.3;
float flowRate;
float flowMilliLitres;
float totalMilliLitres;
float totalLitres;
unsigned long oldTime;

//Water Flow Method
void ICACHE_RAM_ATTR pulseCounter(){
  pulseCount++;
}

void setup() {
  //Debug Console
  Serial.begin(9600);

  //Flow Sensor Var
  pulseCount        = 0;
  flowRate          = 0.0;
  flowMilliLitres   = 0;
  totalMilliLitres  = 0;
  oldTime           = 0; 

  tentatif          = 0;

  //Pin Keluaran Relay dan Masukan Flow Sensor
  pinMode(relay_pin, OUTPUT);
  pinMode(PULSE_PIN, INPUT);

  //Attach Interupt Flow Sensor
  attachInterrupt(PULSE_PIN, pulseCounter, FALLING);

  //Connect Wi-Fi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  WiFi.setSleepMode(WIFI_NONE_SLEEP);
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());

  //Mulai Firebase dan DHT11 dan timeClient
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  dht.begin();
  timeClient.begin();
}

void loop() {
  //GET CONTROL
  
  control = Firebase.getInt("Relay/control");
  if(control!=0){
    digitalWrite(relay_pin,LOW);
    switch(control){
      case 2:
        amountWater = Firebase.getInt("Relay/water/inputManual");
        break;
      case 3:
        amountWater = Firebase.getInt("Relay/water/fuzzyAuto");
        break;
      default:
        amountWater = Firebase.getInt("Relay/water/onoff");
        break;
    }
    delay(250);
    if(totalMilliLitres<amountWater){
      if((millis() - oldTime) > 1000){    // Only process counters once per second
        detachInterrupt(PULSE_PIN);
        flowRate = ((1000.0 / (millis() - oldTime)) * pulseCount) / calibrationFactor;
        oldTime = millis();
        flowMilliLitres = (flowRate / 60) * 2400;
        totalMilliLitres += (flowMilliLitres);     
        totalLitres = totalMilliLitres * 0.001;
        unsigned int frac;   
        Serial.print("flowrate: ");
        Serial.print(int(flowRate));  // Print the integer part of the variable
        Serial.print(".");             // Print the decimal point
        frac = (flowRate - int(flowRate)) * 10;
        Serial.print(frac, DEC) ;      // Print the fractional part of the variable
        Serial.print("L/min");
        Serial.print("  Current Liquid Flowing: ");             // Output separator
        Serial.print(flowMilliLitres/2.4);
        Serial.print("mL/Sec");
        Serial.print("  Output Liquid Quantity: ");             // Output separator
        Serial.print(totalLitres);
        Serial.println("L");
        
        Firebase.setFloat("Water/WaterFlow",flowMilliLitres);
        Firebase.setFloat("Water/WaterSum",totalMilliLitres);
        
        pulseCount = 0;
        
        attachInterrupt(PULSE_PIN, pulseCounter, FALLING);
      }
    }else{
      digitalWrite(relay_pin,HIGH);
      timeClient.update();
      jam = timeClient.getFormattedTime();
      Firebase.setInt("Relay/control",0);
      Firebase.setInt("Water/WaterSum",0);
      Firebase.setInt("Water/WaterFlow",0);
      Firebase.setInt("Riwayat/rwWater/rTentatif",totalMilliLitres);
      Firebase.setInt("Riwayat/rwWater/rwHour/"+jam,totalMilliLitres);
      totalMilliLitres = 0;
    }
  }else{
    digitalWrite(relay_pin,HIGH);
    value_temp    = dht.readTemperature();
    value_mois    = analogRead(analog0);
    value_mois    = map(value_mois,1023,0,0,100);
    timeClient.update();
    jam = timeClient.getFormattedTime();

    delay(250);
    
    Serial.print("Temperature: ");
    Serial.println(value_temp);
    Serial.print("Moisture: ");
    Serial.println(value_mois);

    delay(250);
//    if(abs(value_mois-tentatif)>3){
//      Firebase.setFloat("Realtime/rtSoil",value_mois);
//    }
    Firebase.setFloat("Realtime/rtTemp",value_temp);
    Firebase.setFloat("Realtime/rtSoil",value_mois);
    if(tentatif>5){
      Firebase.setString("Trigger/second",jam);
      tentatif=0;
    }else tentatif++;
    
  }
}

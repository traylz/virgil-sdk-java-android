# Quickstart Java

- [Introduction](#introduction)
- [Obtaining an Access Token](#obtaining-an-access-token)
- [Install](#install)
- [Use case](#use-case)
    - [Initialization](#initialization)
    - [Step 1. Create and Publish the Keys](#step-1-create-and-publish-the-keys)
    - [Step 2. Encrypt and Sign](#step-2-encrypt-and-sign)
    - [Step 3. Send an Email](#step-3-send-an-email)
    - [Step 4. Receive an Email](#step-4-receive-an-email)
    - [Step 5. Get Sender's Card](#step-5-get-senders-card)
    - [Step 6. Verify and Decrypt](#step-6-verify-and-decrypt)
- [See also](#see-also)

## Introduction

This guide will help you get started using the Crypto Library and Virgil Keys Services for the most popular platforms and languages.
This branch focuses on the Java library implementation and covers its usage.

Let's build an encrypted mail exchange system as one of the possible [use cases](#use-case) of Virgil Security Services. ![Use case mail](https://raw.githubusercontent.com/VirgilSecurity/virgil/master/images/Email-diagram.jpg)

## Obtaining an Access Token

First you must create a free Virgil Security developer's account by signing up [here](https://developer.virgilsecurity.com/account/signup). Once you have your account you can [sign in](https://developer.virgilsecurity.com/account/signin) and generate an access token for your application.

The access token provides authenticated secure access to Virgil Keys Services and is passed with each API call. The access token also allows the API to associate your app’s requests with your Virgil Security developer's account.

Use this token to initialize the SDK client [here](#initialization).

## Install

You can easily add SDK dependency to your project.

### Maven

```xml
<dependencies>
  <dependency>
    <groupId>com.virgilsecurity.sdk</groupId>
    <artifactId>client</artifactId>
    <version>3.0</version>
  </dependency>
</dependencies>
```

### Gradle

```
compile 'com.virgilsecurity.sdk:android:3.0@aar'
compile 'com.squareup.retrofit2:retrofit:2.0.0-beta3'
compile 'com.squareup.retrofit2:converter-gson:2.0.0-beta3'
```

## Use Case
**Secure any data end to end**: users need to securely exchange information (text messages, files, audio, video etc) while enabling both in transit and at rest protection. 

- Application generates public and private key pairs using Virgil Crypto library and use Virgil Keys service to enable secure end to end communications:
    - public key on Virgil Public Keys Service;
    - private key on Virgil Private Keys Service or locally.
- Sender’s information is encrypted in Virgil Crypto Library with the recipient’s public key.
- Sender’s encrypted information is signed with his private key in Virgil Crypto Library.
- Application securely transfers the encrypted data, sender’s digital signature and UDID to the recipient without any risk to be revealed.
- Application on the recipient’s side verifies that the signature of transferred data is valid using the signature and sender’s public key in Virgil Crypto Library.
- Received information is decrypted with the recipient’s private key using Virgil Crypto Library.
- Decrypted data is provided to the recipient.

## Initialization

```java
ClientFactory factory = new ClientFactory("{ACCESS_TOKEN}");
``` 

## Step 1. Create and Publish the Keys
First a mail exchange application is generating the keys and publishing them to the Public Keys Service where they are available in an open access for other users (e.g. recipient) to verify and encrypt the data for the key owner.

The following code example creates a new public/private key pair.

```java
String password = "jUfreBR7";
// the private key's password is optional 
KeyPair keyPair = KeyPairGenerator.generate(password);
```

The app is verifying whether the user really owns the provided email address and getting a temporary token for public key registration on the Public Keys Service.

```java
String actionId = factory.getIdentityClient().verify
	(IdentityType.EMAIL, "sender-test@virgilsecurity.com");
// use confirmation code sent to your email box.
ValidatedIdentity identity = factory.getIdentityClient().confirm
	(actionId, "{CONFIRMATION_CODE}");
```

The app is registering a Virgil Card which includes a public key and an email address identifier. The card will be used for the public key identification and searching for it in the Public Keys Service.

```java
VirgilCardTemplate.Builder vcBuilder = 
	new VirgilCardTemplate.Builder().setIdentity(identity).setPublicKey
		(keyPair.getPublic());
VirgilCard senderCard = factory.getPublicKeyClient().createCard
	(vcBuilder.build(), keyPair.getPrivate());
```

## Step 2. Encrypt and Sign
The app is searching for the recipient’s public key on the Public Keys Service to encrypt a message for him. The app is signing the encrypted message with sender’s private key so that the recipient can make sure the message had been sent from the declared sender.

```java
String message = "Encrypt me, Please!!!";

Builder criteriaBuilder = 
	new Builder().setValue("recipient-test@virgilsecurity.com");
List<VirgilCard> recipientCards = 
	factory.getPublicKeyClient().search(criteriaBuilder.build(), 
		keyPair.getPrivate());

Map<String, String> recipients = new HashMap<>();
for (VirgilCard card : recipientCards) {
  recipients.put(card.getId(), card.getPublicKey().getKey());
}

String encryptedMessage = CryptoHelper.encrypt(message, recipients);
String signature = CryptoHelper.sign(encryptedMessage, keyPair.getPrivate());
```

## Step 3. Send an Email
The app is merging the message and the signature into one structure and sending the letter to the recipient using a simple mail client.

```java
JsonObject encryptedBody = new JsonObject();
encryptedBody.addProperty("Content", encryptedMessage);
encryptedBody.addProperty("Signature", signature);
```

## Step 4. Receive an Email
An encrypted letter is received on the recipient’s side using a simple mail client.

```java
JsonObject encryptedBody = "{MESSAGE_BODY}";
```

## Step 5. Get Sender's Card
In order to decrypt the received data the app on recipient’s side needs to get sender’s Virgil Card from the Public Keys Service.

```java
Builder criteriaBuilder = 
	new Builder().setValue("sender-test@virgilsecurity.com");
VirgilCard senderCard = 
	factory.getPublicKeyClient().search(criteriaBuilder.build(), 
		keyPair.getPrivate()).get(0);
```

## Step 6. Verify and Decrypt
We are making sure the letter came from the declared sender by getting his card on Public Keys Service. In case of success we are decrypting the letter using the recipient's private key.

```java
PrivateKey recipientPrivateKey = new PrivateKey("{RECIPIENT_KEY}");

String encryptedContent = encryptedBody.get("Content").getAsString();
String encryptedContentSignature = encryptedBody.get("Signature").getAsString();
boolean isValid = 
	CryptoHelper.verify(encryptedContent, encryptedContentSignature, 
		new PublicKey(senderCard.getPublicKey().getKey()));
if (!isValid) {
  throw new Exception("Signature is not valid.");
}

String originalMessage = CryptoHelper.decrypt
	(encryptedContent, "{RECIPIENT_CARD_ID}", recipientPrivateKey);
```

## See Also

* [Tutorial Crypto Library](crypto.md)
* [Tutorial SDK](public-keys.md)

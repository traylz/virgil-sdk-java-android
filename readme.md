# Java/Android SDK Programming Guide

Welcome to the SDK Programming Guide for Java. This guide is a practical introduction to creating apps for the Java or Android platform that make use of Virgil Security features. The code examples in this guide are written in Java. 

In this guide you will find code for every task you need to implement in order to create an application using Virgil Security. It also includes a description of the main classes and methods. The aim of this guide is to get you up and running quickly. You should be able to copy and paste the code provided into your own apps and use it with minumal changes.

## Table of Contents

* [Setting up your project](#setting-up-your-project)
* [User and App Credentials](#user-and-app-credentials)
* [Creating a Virgil Card](#creating-a-virgil-card)
* [Search for Virgil Cards](#search-for-virgil-cards)
* [Validating Virgil Cards](#validating-virgil-cards)
* [Revoking a Virgil Card](#revoking-a-virgil-card)
* [Operations with Crypto Keys](#operations-with-crypto-keys)
  * [Generate Keys](#generate-keys)
  * [Import and Export Keys](#import-and-export-keys)
* [Encryption and Decryption](#encryption-and-decryption)
  * [Encrypt Data](#encrypt-data)
  * [Decrypt Data](#decrypt-data)
* [Generating and Verifying Signatures](#generating-and-verifying-signatures)
  * [Generating a Signature](#generating-a-signature)
  * [Verifying a Signature](#verifying-a-signature)
* [Authenticated Encryption](#authenticated-encryption)
* [Fingerprint Generation](#fingerprint-generation)
* [Release Notes](#release-notes)

## Setting up your project

The Virgil SDK is provided as set of packages named *com.virgilsecurity.sdk*. Packages are distributed via Maven repository.

### Target

* Java 7+.
* Android API 16+.

### Prerequisites

* Java Development Kit (JDK) 7+
* Maven 3+

### Installing the package

You can easily add SDK dependency to your project, just follow the examples below:

#### Maven

Use this packages for Java projects.
```
<dependencies>
  <dependency>
    <groupId>com.virgilsecurity.sdk</groupId>
    <artifactId>crypto</artifactId>
    <version>4.0.0</version>
  </dependency>
  <dependency>
    <groupId>com.virgilsecurity.sdk</groupId>
    <artifactId>client</artifactId>
    <version>4.0.0</version>
  </dependency>
</dependencies>
```

#### Gradle

Use this packages for Android projects.
```
compile 'com.virgilsecurity.sdk:android:4.0.0@aar'
compile 'com.google.code.gson:gson:2.7'
compile 'org.apache.httpcomponents:httpclient-android:4.3.5.1'
```

## User and App Credentials

When you register an application on the Virgil developer's [dashboard](https://developer.virgilsecurity.com/dashboard), we provide you with an *appID*, *appKey* and *accessToken*.

* **appID** uniquely identifies your application in our services, it is also used to identify the Public key generated in a pair with *appKey*, for example: ```af6799a2f26376731abb9abf32b5f2ac0933013f42628498adb6b12702df1a87```
* **appKey** is a Private key that is used to perform creation and revocation of *Virgil Cards* (Public key) in Virgil services. Also the *appKey* can be used for cryptographic operations to take part in application logic. The *appKey* is generated at the time of creation application and has to be saved in secure place. 
* **accessToken** is a unique string value that provides an authenticated secure access to the Virgil services and is passed with each API call. The *accessToken* also allows the API to associate your app’s requests with your Virgil developer’s account. 

## Connecting to Virgil
Before you can use any Virgil services features in your app, you must first initialize ```VirgilClient``` class. You use the ```VirgilClient``` object to get access to Create, Revoke and Search for *Virgil Cards* (Public keys). 

### Initializing an API Client

To create an instance of *VirgilClient* class, just call its constructor with your application's *accessToken* which you generated on developer's deshboard.
```java
VirgilClient client = new VirgilClient("[YOUR_ACCESS_TOKEN_HERE]");
```

you can also customize initialization using your own context
```java
VirgilClientContext ctx = new VirgilClientContext("[YOUR_ACCESS_TOKEN_HERE]");
ctx.setCardsServiceAddress("https://cards.virgilsecurity.com");
ctx.setReadOnlyCardsServiceAddress("https://cards-ro.virgilsecurity.com");
ctx.setIdentityServiceAddress("https://identity.virgilsecurity.com");

VirgilClient client = new VirgilClient(ctx);
```

### Initializing Crypto

The *VirgilCrypto* class provides cryptographic operations in applications, such as hashing, signature generation and verification, and encryption and decryption.
```java
Crypto crypto = new VirgilCrypto();
```

## Creating a Virgil Card

A *Virgil Card* is the main entity of the Virgil services, it includes the information about the user and his public key. The *Virgil Card* identifies the user/device by one of his types. 

Collect an *appID* and *appKey* for your app. These parametes are required to create a Virgil Card in your app scope. 
```java
String appID = "[YOUR_APP_ID_HERE]";
String appKeyPassword = "[YOUR_APP_KEY_PASSWORD_HERE]";
String appKeyData = "[YOUR_APP_KEY_HERE]";

PrivateKey appKey = crypto.importPrivateKey(appKeyData.getBytes(), appKeyPassword);
```

Generate a new Public/Private keypair using *VirgilCrypto* class. 
```java
KeyPair aliceKeys = crypto.generateKeys();
```

Prepare request
```java
byte[] exportedPublicKey = crypto.exportPublicKey(aliceKeys.getPublicKey());
CreateCardRequest createCardRequest = new CreateCardRequest("alice", "username", exportedPublicKey);
```

then, use *RequestSigner* class to sign request with owner and app keys.
```java
RequestSigner requestSigner = new RequestSigner(crypto);

requestSigner.selfSign(createCardRequest, aliceKeys.getPrivateKey());
requestSigner.authoritySign(createCardRequest, appID, appKey);
```

Publish a Virgil Card
```java
Card aliceCard = client.createCard(createCardRequest);
```

## Search for Virgil Cards
Performs the `Virgil Card`s search by criteria:
- the *Identities* request parameter is mandatory;
- the *IdentityType* is optional and specifies the *IdentityType* of a `Virgil Card`s to be found;
- the *Scope* optional request parameter specifies the scope to perform search on. Either 'global' or 'application'. The default value is 'application';

```java
VirgilClient client = new VirgilClient("[YOUR_ACCESS_TOKEN_HERE]");

SearchCriteria criteria = SearchCriteria.byIdentities(Arrays.asList("alice", "bob"));
List<Card> cards = client.searchCards(criteria);
```

## Validating Virgil Cards

This sample uses *built-in* ```VirgilCardValidator``` to validate cards. By default ```VirgilCardValidator``` validates only *Cards Service* signature. 
```java
// Initialize crypto API
Crypto crypto = new VirgilCrypto();

VirgilCardValidator validator = new VirgilCardValidator(crypto);

// Your can also add another Public Key for verification.
// validator.addVerifier("[HERE_VERIFIER_CARD_ID]", [HERE_VERIFIER_PUBLIC_KEY]);

// Initialize service client
VirgilClient client = new VirgilClient("[YOUR_ACCESS_TOKEN_HERE]");
client.setCardValidator(validator);

try {
    SearchCriteria criteria = SearchCriteria.byIdentities(Arrays.asList("alice", "bob"));
    List<Card> cards = client.searchCards(criteria);
    ...
} catch (CardValidationException ex) {
    // ex.getInvalidCards()
}
```

## Revoking a Virgil Card

Initialize required components.
```java
Crypto crypto = new VirgilCrypto();
VirgilClient client = new VirgilClient("[YOUR_ACCESS_TOKEN_HERE]");

RequestSigner requestSigner = new RequestSigner(crypto);
```

Collect *App* credentials
```java
String appID = "[YOUR_APP_ID_HERE]";
String appKeyPassword = "[YOUR_APP_KEY_PASSWORD_HERE]";
String appKeyData = "[YOUR_APP_KEY_PATH_HERE]";

String appKey = crypto.importPrivateKey(appKeyData.getBytes(), appKeyPassword);
```

Prepare revocation request
```java
String cardId = "[YOUR_CARD_ID_HERE]";

RevokeCardRequest revokeRequest = new RevokeCardRequest(cardId, RevocationReason.UNSPECIFIED);
requestSigner.authoritySign(revokeRequest, appID, appKey);

client.revokeCard(revokeRequest);
```

## Operations with Crypto Keys

### Generate Keys

The following code sample illustrates keypair generation. The default algorithm is ed25519
```java
KeyPair aliceKeys = crypto.generateKeys();
```

### Import and Export Keys

You can export and import your Public/Private keys to/from supported wire representation.

To export Public/Private keys, simply call one of the Export methods:
```java
byte[] exportedPrivateKey = crypto.exportPrivateKey(aliceKeys.getPrivateKey());
byte[] exportedPublicKey = crypto.exportPublicKey(aliceKeys.getPublicKey());
```
 
To import Public/Private keys, simply call one of the Import methods:
```java
PrivateKey privateKey = crypto.importPrivateKey(exportedPrivateKey);
PublicKey publicKey = crypto.importPublicKey(exportedPublicKey);
```

## Encryption and Decryption

Initialize Crypto API and generate keypair.
```java
Crypto crypto = new VirgilCrypto();
KeyPair aliceKeys = crypto.generateKeys();
```

### Encrypt Data
Data encryption using ECIES scheme with AES-GCM. You can encrypt either stream or a byte array.
There also can be more than one recipient

*Byte Array*
```java
byte[] plaintext = "Hello Bob!".getBytes();
byte[] cipherData = crypto.encrypt(plaintext, new PublicKey[] { aliceKeys.getPublicKey() });
```

*Stream*
```java 
try (InputStream in = new FileInputStream([YOUR_FILE_PATH_HERE]);
        OutputStream out = new FileOutputStream("[YOUR_ENCRYPTED_FILE_PATH_HERE]")) {

    crypto.encrypt(in, out, new PublicKey[] { aliceKeys.getPublicKey() });
}
```

### Decrypt Data

You can decrypt either stream or a byte array using your private key

*Byte Array*
```java
byte[] decryptedData = crypto.decrypt(cipherData, aliceKeys.getPrivateKey());
```

*Stream*
```java 
try (InputStream in = new FileInputStream("[YOUR_ENCRYPTED_FILE_PATH_HERE]");
        OutputStream out = new FileOutputStream("[YOUR_DECRYPTED_FILE_PATH_HERE]")) {

    crypto.decrypt(in, out, aliceKeys.getPrivateKey());
}
```

## Generating and Verifying Signatures

This section walks you through the steps necessary to use the *VirgilCrypto* to generate a digital signature for data and to verify that a signature is authentic. 

Generate a new Public/Private keypair and *data* to be signed.
```java
Crypto crypto = new VirgilCrypto();
KeyPair alice = crypto.generateKeys();

byte[] data = "Hello Bob, How are you?".getBytes();
```

### Generating a Signature

Sign the SHA-384 fingerprint of either stream or a byte array using your private key. To generate the signature, simply call one of the sign methods:

*Byte Array*
```java
byte[] signature = crypto.sign(data, alice.getPrivateKey());
```

*Stream*
```java
try (InputStream in = new FileInputStream("[YOUR_FILE_PATH_HERE]")) {

    byte[] signature = crypto.sign(in, alice.getPrivateKey());
}
```

### Verifying a Signature

Verify the signature of the SHA-384 fingerprint of either stream or a byte array using Public key. The signature can now be verified by calling the verify method:

*Byte Array*
```java
boolean isValid = crypto.verify(data, signature, alice.getPublicKey());
```

*Stream*
```java
try (InputStream in = new FileInputStream("[YOUR_FILE_PATH_HERE]")) {

    boolean isValid = crypto.verify(in, signature, alice.getPublicKey());
}
```

## Authenticated Encryption
Authenticated Encryption provides both data confidentiality and data integrity assurances to the information being protected.

```java
Crypto crypto = new VirgilCrypto();

KeyPair alice = crypto.generateKeys();
KeyPair bob = crypto.generateKeys();

// The data to be signed with alice's Private key
String dataToSign = "Hello Bob, How are you?";
byte[] data = dataToSign.getBytes();
```

### Sign then Encrypt
```java
byte[] cipherData = crypto.signThenEncrypt(data, alice.getPrivateKey(), bob.getPublicKey());
```

### Decrypt then Verify
```java
byte[] decryptedData = crypto.decryptThenVerify(cipherData, bob.getPrivateKey(), alice.getPublicKey());
```

## Fingerprint Generation
The default Fingerprint algorithm is SHA-256. The hash is then converted to HEX
```java
Fingerprint fingerprint = crypto.calculateFingerprint("Just a text".getBytes());
```

## Release Notes
 - Please read the latest note here: [https://github.com/VirgilSecurity/virgil-sdk-java-android/releases](https://github.com/VirgilSecurity/virgil-sdk-java-android/releases)

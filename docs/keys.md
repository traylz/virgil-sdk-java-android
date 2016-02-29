# Tutorial Java Keys SDK 

- [Introduction](#introduction)
- [Install](#install)
- [Obtaining an Access Token](#obtaining-an-access-token)
- [Identity Check](#identity-check)
  - [Request Verification](#request-verification)
  - [Confirm and Get an Identity Token](#confirm-and-get-an-identity-token)
- [Cards and Public Keys](#cards-and-public-keys)
  - [Publish a Virgil Card](#publish-a-virgil-card)
  - [Search for Cards](#search-for-cards)
  - [Search for Application Cards](#search-for-application-cards)
  - [Trust a Virgil Card](#trust-a-virgil-card)
  - [Untrust a Virgil Card](#untrust-a-virgil-card)
  - [Revoke a Virgil Card](#revoke-a-virgil-card)
  - [Get a Public Key](#get-a-public-key)
- [Private Keys](#private-keys)
  - [Obtain Key for the Private Keys Service](#obtain-a-service-key)
  - [Stash a Private Key](#stash-a-private-key)
  - [Get a Private Key](#get-a-private-key)
  - [Destroy a Private Key](#destroy-a-private-key)

##Introduction

This tutorial explains how to use the Public Keys Service with SDK library in Java applications. 

##Install

###Gradle
Java

```
compile 'com.virgilsecurity.sdk:client:3.0'
```

Android

```
compile 'com.virgilsecurity.sdk:android:3.0@aar'
compile 'com.squareup.retrofit2:retrofit:2.0.0-beta3'
compile 'com.squareup.retrofit2:converter-gson:2.0.0-beta3'
```

###Maven
Java

```xml
<dependencies>
  <dependency>
    <groupId>com.virgilsecurity.sdk</groupId>
    <artifactId>client</artifactId>
    <version>3.0</version>
  </dependency>
</dependencies>
```

##Obtaining an Access Token

First you must create a free Virgil Security developer's account by signing up [here](https://virgilsecurity.com/account/signup). Once you have your account you can [sign in](https://virgilsecurity.com/account/signin) and generate an access token for your application.

The access token provides an authenticated secure access to the Public Keys Service and is passed with each API call. The access token also allows the API to associate your app’s requests with your Virgil Security developer's account.

Simply add your access token to the client factory.

```java
ClientFactory factory = new ClientFactory("{ACCESS_TOKEN}");
``` 

## Identity Check

All the Virgil Security services are strongly interconnected with the Identity Service. It determines the ownership of the identity being checked using particular mechanisms and as a result it generates a temporary token to be used for the operations which require an identity verification. 

#### Request Verification

Initialize the identity verification process.

Java

```java
String actionId = factory.getIdentityClient().verify
	(IdentityType.EMAIL, "{YOU EMAIL}");
```

Android

```java
String email = "{EMAIL}";

try {
  factory.getIdentityClient().verify
  	(IdentityType.EMAIL, email, new ResponseCallback<Action>() {
	    @Override
	    public void onSuccess(Action action) {
	      // Obtain action identifier
	      ...
	    }
    
    @Override
    public void onFailure(APIError apiError) {
      // Process failure
      ...
    }
  });
} catch (IOException e) {
  // Handle exception
  ...
}
```

#### Confirm and Get an Identity Token

Confirm the identity and get a temporary token.

Java

```java
ValidatedIdentity identity = 
	factory.getIdentityClient().confirm(actionId, "{CONFIRMATION CODE}");
```

Android

```java
String actionId = "{ACTION_ID}";
String confirmationCode = {CONFIRMATION_CODE};

try {
  factory.getIdentityClient().confirm
  	(actionId, confirmationCode, new ResponseCallback<ValidatedIdentity>() {
	    @Override
	    public void onSuccess(ValidatedIdentity validatedIdentity) {
	      // Obtain validation token
	      ...
	    }
    
    @Override
    public void onFailure(APIError apiError) {
      // Process failure
      ...
    }
  });
} catch (IOException e) {
  // Handle exception
  ...
}
```

## Cards and Public Keys

A Virgil Card is the main entity of the Public Keys Service, it includes the information about the user and his public key. The Virgil Card identifies the user by one of his available types, such as an email, a phone number, etc.

#### Publish a Virgil Card

An identity token which can be received [here](#identity-check) is used during the registration.

Java

```java
KeyPair keyPair = KeyPairGenerator.generate();
VirgilCard card = factory.getPublicKeyClient().createCard(identity, keyPair.getPublic(), keyPair.getPrivate());
```

Android

```java
KeyPair keyPair = KeyPairGenerator.generate();
PrivateKey privateKey = keyPair.getPrivateKey();

String accessToken = "{ACCESS_TOKEN}";

ValidatedIdentity identity = new ValidatedIdentity();
identity.setType(IdentityType.EMAIL);
identity.setValue("{EMAIL}");

VirgilCardTemplate.Builder vcBuilder = 
 new VirgilCardTemplate.Builder().setIdentity(identity).setPublicKey(publicKey);

ResponseCallback<VirgilCard> callback = new ResponseCallback<VirgilCard>() {
  @Override
  public void onSuccess(VirgilCard virgilCard) {
    // Virgil Card created
    ...
  }
  
  @Override
  public void onFailure(APIError apiError) {
      // Process failure
      ...
  }
};

clientFactory.getPublicKeyClient().createCard
	(vcBuilder.build(), privateKey, callback);
```

#### Search for Cards

Search for the Virgil Card by provided parameters.

Java

```java
Builder criteriaBuilder = 
	new Builder().setValue("EMAIL ADDRESS").setIncludeUnconfirmed(true);
List<VirgilCard> cards = 
	factory.getPublicKeyClient().search
		(criteriaBuilder.build(), keyPair.getPrivate());
```
Android

```java
ResponseCallback<List<VirgilCard>> callback = 
	new ResponseCallback<List<VirgilCard>>() {
	  @Override
	  public void onSuccess(List<VirgilCard> virgilCards) {
	    // Process list of Virgil Cards
	    ...
	  }
  
  @Override
  public void onFailure(APIError apiError) {
    // Process failure
    ...
  }
};

Builder criteriaBuilder = 
	new Builder().setValue("EMAIL ADDRESS").setIncludeUnconfirmed(true);
clientFactory.getPublicKeyClient().search
	(criteriaBuilder.build(), privateKey, callback);
```

#### Search for Application Cards

Search for the Virgil Cards by a defined pattern. The example below returns a list of applications for Virgil Security company.

Java

```java
SearchCriteria criteria = new SearchCriteria().setValue(appId);

List<VirgilCard> appCards = 
 factory.licKeyClient().searchApp(criteriaBuilder.build(), keyPair.getPrivate());
```
Android

```java
ResponseCallback<List<VirgilCard>> callback = 
new ResponseCallback<List<VirgilCard>>() {
  @Override
  public void onSuccess(List<VirgilCard> virgilCards) {
    // Process list of Virgil Cards
    ...
  }
  
  @Override
  public void onFailure(APIError apiError) {
    // Process failure
    ...
  }
};

SearchCriteria criteria = new SearchCriteria();
criteria.setValue("APPLICATION_ID");
clientFactory.getPublicKeyClient().searchApp(criteria, privateKey,callback);
```

#### Trust a Virgil Card

Any Virgil Card user can act as a certification center within the Virgil Security ecosystem. Every user can certify another's Virgil Card and build a net of trust based on it.

The example below demonstrates how to certify a user's Virgil Card by signing its hash attribute. 

Java

```java
String signedCardId = "VIRGIL CARD ID";
String signedCardHash = "VIRGIL CARD HASH";

SignResponse signData = 
	factory.getPublicKeyClient().signCard(signedCardId, signedCardHash, 
		cardInfo.getId(), keyPair.getPrivate());
```

Android

```java
String signedCardId = "VIRGIL CARD ID";
String signedCardHash = "VIRGIL CARD HASH";

clientFactory.getPublicKeyClient().signCard
	(signedCardId, signedCardHash, signerCardId, privateKey, 
		new ResponseCallback<SignResponse>() {
		  @Override
		  public void onSuccess(SignResponse signResponse) {
		    // Virgil Card trusted
		    ...
		  }
  
  @Override
  public void onFailure(APIError apiError) {
    // Process failure
    ...
  }
});
```

#### Untrust a Virgil Card

Naturally it is possible to stop trusting the Virgil Card owner as in all relations. This is not an exception in Virgil Security system.

Java

```java
factory.getPublicKeyClient().unsignCard(signedCardId, cardInfo.getId(), keyPair.getPrivate());
```
Android

```java
clientFactory.getPublicKeyClient().unsignCard
	(signedCardId, signerCardId, privateKey, new VoidResponseCallback() {
	  @Override
	  public void onSuccess(boolean b) {
	    // Process unsign result
	    ...
	  }
  
  @Override
  public void onFailure(APIError apiError) {
    // Process failure
    ...
  }
});
```

#### Revoke a Virgil Card

This operation is used to delete the Virgil Card from the search and mark it as deleted.
 
Java

```java
factory.getPublicKeyClient().deleteCard
	(identity, cardInfo.getId(), keyPair.getPrivate());
```

Android

```java
ValidatedIdentity identity = new ValidatedIdentity();
identity.setType(IdentityType.EMAIL);
identity.setValue(email);

clientFactory.getPublicKeyClient().deleteCard
	(identity, cardId, privateKey, password, new VoidResponseCallback() {
		  @Override
		  public void onSuccess(boolean b) {
		    // Process result
		    ...
		  }
  
  @Override
  public void onFailure(APIError apiError) {
    // Process failure
    ...
  }
});
```

#### Get a Public Key

Gets a public key from the Public Keys Service by the specified ID.

Java

```java
PublicKeyInfo publicKey = factory.getPublicKeyClient().getKey(cardInfo.getPublicKey().getId());
```
Android

```java
String publicKeyId = "{PUBLIC_KEY_ID}";
clientFactory.getPublicKeyClient().getKey
	(publicKeyId, new ResponseCallback<PublicKeyInfo>() {
		  @Override
		  public void onSuccess(PublicKeyInfo keyInfo) {
		    // Process result
		    ...
		  }
  
  @Override
  public void onFailure(APIError apiError) {
    // Process failure
    ...
  }
});
```

## Private Keys

The security of private keys is crucial for the public key cryptosystems. Anyone who can obtain a private key can use it to impersonate the rightful owner during all communications and transactions on intranets or on the internet. Therefore, private keys must be in the possession only of authorized users, and they must be protected from unauthorized use.

Virgil Security provides a set of tools and services for storing private keys in a safe storage which lets you synchronize your private keys between the devices and applications.

Usage of this service is optional.

####Obtain Key for the Private Keys Service

Java

```java
criteria = new SearchCriteria();
criteria.setValue("com.virgilsecurity.private-keys");

VirgilCard serviceCard = factory.getPublicKeyClient().searchApp
	(criteria, keyPair.getPrivate()).get(0);
```

Android

```java
ResponseCallback<List<VirgilCard>> callback = 
	new ResponseCallback<List<VirgilCard>>() {
		  @Override
		  public void onSuccess(List<VirgilCard> virgilCards) {
		    VirgilCard serviceCard = virgilCards.get(0);
		    // Store Service Card
		    ...
		  }
  
  @Override
  public void onFailure(APIError apiError) {
    // Process failure
    ...
  }
};

SearchCriteria criteria = new SearchCriteria();
criteria.setValue("com.virgilsecurity.private-keys");
clientFactory.getPublicKeyClient().searchApp(criteria, privateKey,callback);
```

#### Stash a Private Key

Private key can be added for storage only in case you have already registered a public key on the Public Keys Service.

Use the public key identifier on the Public Keys Service to save the private keys. 

The Private Keys Service stores private keys the original way as they were transferred. That's why we strongly recommend to trasfer the keys which were generated with a password.

Java

```java
factory.getPrivateKeyClient(serviceCard).stash
	(cardInfo.getId(), keyPair.getPrivate());
```

Android

```java
factory.getPrivateKeyClient(serviceCard).stash
	(cardInfo.getId(), keyPair.getPrivate(), new VoidResponseCallback() {
		  @Override
		  public void onSuccess(boolean result) {
		    // Process operation result
		    ...
		  }
  
  @Override
  public void onFailure(APIError error) {
    // Process failure
    ...
  }
});
```

#### Get a Private Key

To get a private key you need to pass a prior verification of the Virgil Card where your public key is used.

Java
  
```java
actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, email);
// use confirmation code that has been sent to you email box.
identity = factory.getIdentityClient().confirm(actionId, "{CONFIRMATION_CODE}");
		
PrivateKeyInfo privateKey = factory.getPrivateKeyClient(serviceCard).get
	(cardInfo.getId(), identity);
```

Android

```java
// Obtain verified identity first
factory.getPrivateKeyClient(serviceCard).get
	(cardInfo.getId(), identity, new ResponseCallback<PrivateKeyInfo>() {
  
		  @Override
		  public void onSuccess(PrivateKeyInfo keyInfo) {
		    // Process private key
		    ...
		  }
  
  @Override
  public void onFailure(APIError error) {
    // Process failure
    ...
  }
});
```

#### Destroy a Private Key

This operation deletes the private key from the service without a possibility to be restored. 

Java
  
```java
factory.getPrivateKeyClient(serviceCard).destroy
	(cardInfo.getId(), keyPair.getPrivate());
```

Android

```java
factory.getPrivateKeyClient(serviceCard).destroy
	(cardInfo.getId(), keyPair.getPrivate(), new VoidResponseCallback() {
  
		  @Override
		  public void onSuccess(boolean result) {
		    // Process operation result
		    ...
		  }
  
  @Override
  public void onFailure(APIError error) {
    // Process failure
    ...
  }
});
```

## See Also

* [Quickstart](quickstart.md)
* [Android tutorial](keys-android.md)
* [Reference API for SDK](sdk-reference-api.md)

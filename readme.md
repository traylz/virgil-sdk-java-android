# Virgil SDK Java

##Build

```
mvn clean package source:jar javadoc:jar -DskipTests=true
```

##Build with functional tests

```
mvn clean package source:jar javadoc:jar -DskipTests=true -DAPPLICATION_ID={application_id} -DSERVICE_ACCOUNT={mailinator_account}
-DCLIENT_ACCOUNT={mailinator_account} -DACCESS_TOKEN={access_token}
```

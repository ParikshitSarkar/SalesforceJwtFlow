# SalesforceJwtFlow
Retrieval of Access token from Salesforce (connected app) using JWT Bearer flow


Steps to follow - 

1) Create x509 certificate (to be uploaded in sf connected app)

i) Generate jks file 

keytool -genkey -keyalg RSA -alias selfsigned -keystore key.jks -storepass password -validity 360 -keysize 1024

Note keystore pwd and alias : 

keystore password = password 

alias = certalias 


ii) Convert jks to pkcs12 using keytool

keytool -importkeystore -srckeystore key.jks -destkeystore keystore.p12 -srcstoretype JKS -deststoretype PKCS12 -srcstorepass password -deststorepass password -srcalias certalias -destalias certalias -srckeypass password -destkeypass password -noprompt


This will create keystore.p12 keystore which is  PKCS12 typ


iii) Extract public certificate from keystore.p12 to pem using openssl

openssl pkcs12 -in keystore.p12 -out my_key_store.pem

This will create my_key_store.pem file with public certificate

iv)  Convert pem to crt using openssl 

openssl x509 -outform der -in my_key_store.pem -out key_store_cert.crt

2) Upload key_store_cert.crt to SF connected app

3) Assign the connected app to the particular user profiles as needed 

4) Generate the JWT token using java program (Ref: https://help.salesforce.com/articleView?id=remoteaccess_oauth_jwt_flow.htm&type=5)

5) Run the following command with CURL

curl --data "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=Your_JWT_Token" --header "Content-Type: application/x-www-form-urlencoded" --request POST https://[Your_SF_Domain].my.salesforce.com/services/oauth2/token


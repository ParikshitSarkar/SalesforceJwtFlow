import org.apache.commons.codec.binary.Base64;
import java.io.*;
import java.security.*;
import java.text.MessageFormat;

public class JwtTokenGenerator {

    public static void main(String[] args) {

        String header = "{\"alg\":\"RS256\"}";
        String claimTemplate = "'{'\"iss\": \"{0}\", \"sub\": \"{1}\", \"aud\": \"{2}\", \"exp\": \"{3}\"'}'";

        try {
            StringBuffer token = new StringBuffer();

            //Encode the JWT Header and add it to our string to sign
            token.append(Base64.encodeBase64URLSafeString(header.getBytes("UTF-8")));

            //Separate with a period
            token.append(".");

            //Create the JWT Claims Object
            String[] claimArray = new String[4];
            claimArray[0] = ""; //Enter client id/consumer key generated from Connected App
            claimArray[1] = ""; //Enter your SF username
            claimArray[2] = "https://test.salesforce.com"; //For production enter : https://login.salesforce.com
            claimArray[3] = Long.toString( ( System.currentTimeMillis()/1000 ) + 1000);

            MessageFormat claims = new MessageFormat(claimTemplate);
            String payload = claims.format(claimArray);

            //Add the encoded claims object
            token.append(Base64.encodeBase64URLSafeString(payload.getBytes("UTF-8")));

            //Load the private key from a keystore
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(new FileInputStream("keystore/key.jks"), "password".toCharArray());
            PrivateKey privateKey = (PrivateKey) keystore.getKey("certalias", "password".toCharArray());

            //Sign the JWT Header + "." + JWT Claims Object
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(token.toString().getBytes("UTF-8"));
            String signedPayload = Base64.encodeBase64URLSafeString(signature.sign());

            //Separate with a period
            token.append(".");

            //Add the encoded signature
            token.append(signedPayload);

            System.out.println("JWT Token = " + token.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

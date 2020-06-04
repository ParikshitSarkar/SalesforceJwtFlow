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
            claimArray[0] = "3MVG9FxR3Tq3eZN_acTBt32b5XbHCskLR0aMuvkqIbhQL7QKl9jNbjs2XXZYvwtbL8goTwjochumwzXdrjS7P";
            claimArray[1] = "sarkarp@cadence.com.sarkarp";
            claimArray[2] = "https://test.salesforce.com";
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

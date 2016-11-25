
import java.io.*;
import java.net.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import java.security.*;
import java.security.spec.*;
import javax.crypto.Cipher;


import javax.crypto.Cipher;
import java.util.Base64;
import javax.crypto.SealedObject;

public class tryPKenc {

 public static void main(String[] args) {

    try {

		// Get an instance of the RSA key generator
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		// Generate the keys — might take sometime on slow computers
		KeyPair myPair = kpg.generateKeyPair();

		Cipher c = Cipher.getInstance("RSA");
		// Initiate the Cipher, telling it that it is going to Encrypt, giving it the public key
		c.init(Cipher.ENCRYPT_MODE, myPair.getPublic());

		// Create a secret message
		String theSharedKey = new String("Bar12345Bar12345Bar12345Bar12345");
		// Encrypt that message using a new SealedObject and the Cipher we created before
		SealedObject myEncryptedMessage= new SealedObject( theSharedKey, c);

		// HERE IS WHERE WE SENT OVER TCP


		Cipher dec = Cipher.getInstance("RSA");
		// Initiate the Cipher, telling it that it is going to Decrypt, giving it the private key
		dec.init(Cipher.DECRYPT_MODE, myPair.getPrivate());
		String message = (String) myEncryptedMessage.getObject(dec);	

		System.out.println("foo = " + message);
		
		
		// THIS IS PRESEND:
      	byte [] array = myPair.getPublic().getEncoded();
		String pkas = Base64.getEncoder().encodeToString(array);

		System.out.println("Presend public Key as a string = " + pkas );
		System.out.println("It\'s length = " + array.length);


		// THIS IS POST SEND TO GET IT BACK TO ITS ORIGININAL FORM
		// here we change it back from a string which we'll do on the client side again...
		byte[] originalPublicKey = Base64.getDecoder().decode(pkas);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(originalPublicKey);      
        KeyFactory keyFact = KeyFactory.getInstance("RSA");
        PublicKey pubKey2 = keyFact.generatePublic(x509KeySpec);

		System.out.println("\n\n\n");
        // SERVER SIDE OUTPUT
        byte [] serverArray = pubKey2.getEncoded();
		String spkas = Base64.getEncoder().encodeToString(serverArray);

		System.out.println("Postsend public Key as a string = " + spkas );
		System.out.println("It\'s length = " + serverArray.length);

    } catch (Exception e){}

}

}
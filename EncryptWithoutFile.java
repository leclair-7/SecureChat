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



public class EncryptWithoutFile {
 
   /**
   * Generate key which contains a pair of private and public key using 1024
   * bytes. Store the set of keys in Prvate.key and Public.key files.
   *  
   * @throws NoSuchAlgorithmException
   * @throws IOException
   * @throws FileNotFoundException
   */

  /**
   * Encrypt the plain text using public key.
   * 
   * @param text
   *          : original plain text
   * @param key
   *          :The public key
   * @return Encrypted text
   * @throws java.lang.Exception
   */
  public static byte[] encrypt(String text, PublicKey key) {
    byte[] cipherText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance(ALGORITHM);
      // encrypt the plain text using the public key
      cipher.init(Cipher.ENCRYPT_MODE, key);
      cipherText = cipher.doFinal(text.getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return cipherText;
  }

  /**
   * Decrypt text using private key.
   * 
   * @param text
   *          :encrypted text
   * @param key
   *          :The private key
   * @return plain text
   * @throws java.lang.Exception
   */
  public static String decrypt(byte[] text, PrivateKey key) {
    byte[] dectyptedText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance(ALGORITHM);

      // decrypt the text using the private key
      cipher.init(Cipher.DECRYPT_MODE, key);
      dectyptedText = cipher.doFinal(text);

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return new String(dectyptedText);
  }

  /**
   * Test the EncryptionUtil
   */
public static final String ALGORITHM = "RSA";


  public static void main(String[] args) {

    try {
      // Check if the pair of keys are present else generate those.  
     
      final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
      keyGen.initialize(1024);
      final KeyPair key = keyGen.generateKeyPair();
      
      PublicKey publicKey  = key.getPublic();
      PrivateKey privateKey=key.getPrivate();
      byte [] array = publicKey.getEncoded();
      System.out.println("pubKeyasString: "
                    + Base64.getEncoder().encodeToString(array));
      String publicKeyAsString = Base64.getEncoder().encodeToString(array);
      

      final String originalText = "Text to be scrambled and all that jazz";      
      
      final byte[] cipherText = encrypt(originalText, publicKey);

      // Decrypt the cipher text using the private key.
      final String plainText = decrypt(cipherText, privateKey);

      // Printing the Original, Encrypted and Decrypted Text and how many bytes in cipherText
      System.out.println("Original Text: " + originalText);
      System.out.println("Encrypted Text: " +cipherText.toString() + " xx: " + cipherText.length);
      System.out.println("Decrypted Text: " + plainText);
  
      byte[] originalPublicKey = Base64.getDecoder().decode(publicKeyAsString);
      X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(originalPublicKey);
      
      KeyFactory keyFact = KeyFactory.getInstance("RSA");
      PublicKey pubKey2 = keyFact.generatePublic(x509KeySpec);

      if ( pubKey2.equals(publicKey))
      {
          System.out.println("out of danger");
      }
      byte [] array2 = pubKey2.getEncoded();
      System.out.println("pubKeyasString: "
                    + Base64.getEncoder().encodeToString(array2));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
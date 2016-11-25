
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


public class SecureChatUtils
{


    public static String hashPS( String password) throws NoSuchAlgorithmException
    {    
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      
      // I'm not sure what the hell this update thing does
      md.update(password.getBytes() );

      //this computes the hash
      byte byteData[] = md.digest();

      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < byteData.length; i++) {
       sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
      }  
      
      return sb.toString();
    } 

  public static String hashBuddyList( LinkedList <String> aBuddyList) throws NoSuchAlgorithmException
  {    
       
        String buddyListAsString = "";
        int numit =0;
        for(String o : aBuddyList)
        {
            //System.out.println(o);
            if ( numit == 0) 
            { 
                buddyListAsString = o; 
                numit =14; 
            } 
            else 
            {
                buddyListAsString = buddyListAsString + "\t" + o;   
            }
        }
    String hashBuddyListNames = hashOfBuddyList.hashPS( buddyListAsString );
    //System.out.println( buddyListAsString + ": " + hashBuddyListNames );
    
    //String [] talferd = { buddyListAsString, "ACK", hashBuddyListNames } ;
    return buddyListAsString + "\t" + "ACK_X1" + "\t" + hashBuddyListNames;
  }



  public static final String ALGORITHM = "RSA";

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
      cipher.init(Cipher.DECRYPT_MODE, key);
      dectyptedText = cipher.doFinal(text);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return new String(dectyptedText);
  }

  static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  static SecureRandom rnd = new SecureRandom();

  public static String nonce( int len )
  {
   StringBuilder sb = new StringBuilder( len );
   for( int i = 0; i < len; i++ ) 
      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
   return sb.toString();
  }



}


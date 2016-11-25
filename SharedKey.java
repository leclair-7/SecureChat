import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Lucas Hagel on 9/21/2015. After he gave up on doing this in Python
 */

/*
import java.util.HashSet;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
*/
import java.io.*;
import java.net.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/*
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
*/
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

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SealedObject;



import java.util.Base64;

public class SharedKey {
    
    public static String key = "Bar12345Bar12345Bar12345Bar12345"; // 256 bit key
    public static String initVector = "RandomInitVector"; // 16 bytes IV


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
        
        // that v v v v outputs 32 as it should
        //System.out.println( byteData.length);
        //System.out.println("Hex format : " + sb.toString());
        return sb.toString();
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

    public static String encrypt(String value, String key, String initVector) {
        //System.out.println("import worked Shared Key");
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            
            //             mode             -   key  - IV
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            /*
            System.out.println("encrypted string: "
                    + Base64.getEncoder().encodeToString(encrypted));
            */
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encrypted, String key, String initVector) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        
        String key = "Bar12345Bar12345Bar12345Bar12345"; // 256 bit key
        String initVector = "RandomInitVector"; // 16 bytes IV

        // this message is long to see if CBC is working among other things
        String message = "Perhaps you will tire sooner than he will. It is a sad thing to think of, but there is no doubt that genius lasts longer than beauty. That accounts for the fact that we all take such pains to over-educate ourselves. In the wild struggle for existence, we want to have something that endures, and so we fill our minds with rubbish and facts, in the silly hope of keeping our place. The thoroughly well-informed man—that is the modern ideal. And the mind of the thoroughly well-informed man is a dreadful thing. It is like a bric-a-brac shop, all monsters and dust, with everything priced above its proper value. I think you will tire first, all the same. Some day you will look at your friend, and he will seem to you to be a little out of drawing, or you won't like his tone of colour, or something. You will bitterly reproach him in your own heart, and seriously think that he has behaved very badly to you. The next time he calls, you will be perfectly cold and indifferent. It will be a great pity, for it will alter you. What you have told me is quite a romance, a romance of art one might call it, and the worst of having a romance of any kind is that it leaves one so unromantic.";
        
        //System.out.println(decrypt(encrypt(message, key, initVector), key, initVector ));
        System.out.println("\n\n\n");
        System.out.println(key.length());

        try{
            //hashPS takes a string and hashes it
            System.out.println(SharedKey.hashPS(key)+"\n\n"+SharedKey.hashPS( key ).length() );
            String logicalKnife = SharedKey.hashPS(nonce(10)).substring(0,32);
            System.out.println(logicalKnife + "\n\n" + logicalKnife.length() );
            System.out.println(decrypt(encrypt(message, logicalKnife, initVector), logicalKnife, initVector ));

        } catch (Exception excep){} 
    
    }
    
} 

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

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SealedObject;

public class userInfo
  {
      private LinkedList<String> buddyList;
      private String passwordHash;

      public userInfo ( String [] buddies)
      {        
        buddyList = new LinkedList<String>( Arrays.asList(buddies)) ;
        
        try{          
            passwordHash = TCPServer.hashPS( buddyList.get(1) );
        } catch ( NoSuchAlgorithmException e) { 
          e.printStackTrace(); 
        }

        if ( buddies.length == 2)
        {
          buddyList = null;
        }
        else 
        {
          // user and his/her PS is not a buddy
          buddyList.remove();
          buddyList.remove();        
        }
      }
      public LinkedList <String> getBuddyList() { return buddyList; }
      public String getPS() { return passwordHash; }
      public String toString()
      {
          String buddies = "";
          int tag =0;

          if (buddyList == null || buddyList.size() ==0 )
          {
              buddies = "no friends, must be Jerry";
          }
          else if ( buddyList.size() ==1 )
          {
              buddies = buddyList.get(0);
          }
          else
          {
            for(String people : buddyList ) {
              if ( tag == 0)
              { tag = 9;
                buddies = people;
              }
              else
              {
              buddies = buddies + ", " + people;
              }
            } 
          }
            
        return buddies;
      }
  }
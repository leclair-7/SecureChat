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
import java.util.Base64;
import javax.crypto.SealedObject;

class hashOfBuddyList
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
    
    // that v v v v outputs 32 as it should
    //System.out.println( byteData.length);
    //System.out.println("Hex format : " + sb.toString());
    return sb.toString();
  }

  public static String[] hashBuddyList( LinkedList <String> aBuddyList) throws NoSuchAlgorithmException
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
    
    String [] talferd = { buddyListAsString, hashBuddyListNames } ;
    return talferd;
  }


private static HashMap < String, userInfo > userProfiles;

 public static void main(String[] args) {

//    NumPeople =0;
        userProfiles = new HashMap<>();
    
        String fileName = "chatusers.txt";    
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
          String line;
          while ((line = br.readLine()) != null) {
            String[] line_arr = line.split("\\s+");
            //System.out.println(line_arr[0] + line_arr.length);
            userProfiles.put( line_arr[0], new userInfo(line_arr) );
          }
        } catch (IOException e) {
          e.printStackTrace();
        }

        // want to print userProfiles info for diagnostics
        

        String allNames = "";
        /*
        for (String name: userProfiles.keySet())
        {            
    		allNames = allNames + name;

            String key = name;
            String value = userProfiles.get(name).toString();  
            //System.out.println(key + "'s buddies are: " + value);
            System.out.println(key + "'s password hash is: " + userProfiles.get(name).getPS());
            //	 System.out.println( userProfiles.get(name).getPS().length() + " \n");
        }
		*/
   
        try{
		LinkedList <String> aBuddyList = userProfiles.get("Ron").getBuddyList();
		String buddyListAsString = "";
		int numit =0;
		for(String o : aBuddyList)
		{
		    System.out.println(o);
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
		String hashBuddyListNames = hashOfBuddyList.hashPS( buddyListAsString);
		System.out.println( buddyListAsString + ": " + hashBuddyListNames );


        System.out.println("\nSecond:\n");
        
        System.out.println(hashBuddyList(aBuddyList)[0] +": "+ hashBuddyList(aBuddyList)[1] );
	}catch ( Exception exce){}
	

    } // ends main
} // end of hashOfBuddyList
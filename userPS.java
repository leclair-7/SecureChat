import java.io.*;
import java.net.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

class userPS
{
  /*
  *   Params: the user's password
  *
  *   Returns: String that is a String of the hex of the hash 
  */
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

 public static void main(String argv[]) throws Exception
 {    
    HashMap < String, userInfo > userProfiles = new HashMap<>();
    
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

    // want to print userProfiles
    for (String name: userProfiles.keySet())
    {
        String key = name;
        String value = userProfiles.get(name).toString();  
        //System.out.println(key + "'s buddies are: " + value);
        System.out.println(key + "'s password has is: " + userProfiles.get(name).getPS());
    } 
  }
}

 /*
  *  We just won't account for passing in a String array < 2
  *
  *  Functions: getBuddyList
  */
   class userInfo
  {
      private LinkedList<String> buddyList;
      private String passwordHash;

      public userInfo ( String [] buddies)
      {        
        buddyList = new LinkedList<String>( Arrays.asList(buddies)) ;
        
        try{          
            passwordHash = userPS.hashPS( buddyList.get(1) );
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